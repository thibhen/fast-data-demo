package proxy.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.CharStreams;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import org.bouncycastle.util.Arrays.Iterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.kafka.core.KafkaTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static com.netflix.zuul.context.RequestContext.getCurrentContext;
import static org.springframework.util.ReflectionUtils.rethrowRuntimeException;

@Component
public class Filter extends ZuulFilter {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger LOGGER =
        LoggerFactory.getLogger(Filter.class);


    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
         boolean shouldFilter = true;

        if( getCurrentContext() != null && 
            getCurrentContext().getOriginResponseHeaders() != null ){
            // Skip images from the filter
            for(Pair<String,String> pair : getCurrentContext().getOriginResponseHeaders()){
                if( "Content-Type".equals(pair.first()) && pair.second().contains("image") ){
                        shouldFilter = false;
                }
            }
        }
        return shouldFilter;
    }

    private String extractJsonFromHttp() throws IOException {
        RequestContext context = getCurrentContext();
              
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        
        // Processing Request Headers
        ObjectNode requestNode = mapper.createObjectNode();
        rootNode.set("request",requestNode);
        ObjectNode headerNode = mapper.createObjectNode();
        Collections
            .list(context.getRequest().getHeaderNames())
            .stream()
            .forEach( x -> { headerNode.put(x,context.getRequest().getHeader(x));} );
        requestNode.set("headers",headerNode);
        
        
        // Processing Parameters
        ObjectNode paramNode = mapper.createObjectNode();
        Collections
            .list(context.getRequest().getParameterNames())
            .stream()
            .forEach( x -> { paramNode.put(x,context.getRequest().getParameter(x));} );
        requestNode.set("parameters",paramNode);
       
        // Processing Response Body
        ObjectNode responseNode = mapper.createObjectNode();
        rootNode.set("response",responseNode);
        InputStream responseStream = context.getResponseDataStream();
        String responseBody = StreamUtils.copyToString(responseStream, Charset.forName("UTF-8"));
        responseNode.put("body",responseBody);
        
        // Processing Request Body
        BufferedReader requestReader = context.getRequest().getReader();
        String requestBody = CharStreams.toString(requestReader);
        responseNode.put("body",requestBody);
        
        // Processing other stuff
        requestNode.put("method",context.getRequest().getMethod());
        requestNode.put("contentType",context.getRequest().getContentType());
        requestNode.put("remoteAddr",context.getRequest().getRemoteAddr());
        requestNode.put("servletPath",context.getRequest().getServletPath());
        requestNode.put("sessionId",context.getRequest().getRequestedSessionId());
        responseNode.put("status",context.getResponse().getStatus());

        // Processing Response Headers
        ObjectNode respHeaderNode = mapper.createObjectNode();
        //context.getResponse().getHeaderNames()

        context.getOriginResponseHeaders()
            .stream()
            .forEach( x -> { respHeaderNode.put(x.first(),x.second());} );
        responseNode.set("headers",respHeaderNode);
        
        // Create Output
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        

        context.setResponseBody(responseBody);

        return jsonString;
    }

    @Override
    public Object run() {
		
		try {
            String jsonStuff = extractJsonFromHttp();
            
            LOGGER.info(String.format("%-100s",jsonStuff));
        
            kafkaTemplate.send("raw-http", 
                getCurrentContext().getRequest().getRequestedSessionId()  ,
                jsonStuff);
		}
		catch (IOException e) {
            rethrowRuntimeException(e);
		}
		return null;
    } 

}
