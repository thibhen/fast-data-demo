package fastdatademo.stream;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MySerde implements Serde<JsonNode> {


    public static class MySerializer implements Serializer<JsonNode> {

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
        }
    
        @Override
        public byte[] serialize(String topic, JsonNode data) {
            return data == null ? null : data.toString().getBytes();
        }
    
        @Override
        public void close() {
        }
    }


    public static class MyDeserializer implements Deserializer<JsonNode> {

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
    

    
    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<JsonNode> serializer() {
        return new MySerializer();
    }

    @Override
    public Deserializer<JsonNode> deserializer() {
        return new MyDeserializer();
    }

   

}
