package fastdatademo.stream;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;


public class MyDeserializer implements Deserializer<JsonNode> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    private final static ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public JsonNode deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readTree(data);
        } catch (IOException e) {
            return null;
		}
    }

    @Override
    public void close() {

    }

  
}
