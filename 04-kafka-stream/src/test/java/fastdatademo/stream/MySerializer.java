package fastdatademo.stream;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.common.serialization.Serializer;

public class MySerializer implements Serializer<JsonNode> {

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
