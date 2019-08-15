package fastdatademo.stream;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;


public class MySerde implements Serde<JsonNode> {

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
