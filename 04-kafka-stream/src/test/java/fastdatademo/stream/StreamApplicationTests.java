package fastdatademo.stream;

import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;

import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Configuration
@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test")
public class StreamApplicationTests {

    @Value("classpath:test4.json")
    Resource testFile;

    @Bean
    public String message() throws IOException {
        return new String(Files.readAllBytes(testFile.getFile().toPath()));
    }

    @Autowired
    Environment env;
    
    @Bean
    public Properties properties() throws IOException {
        Properties properties = new Properties();
        Arrays.asList(
        StreamsConfig.APPLICATION_ID_CONFIG, 
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, 
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, 
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG)
            .stream()
            .forEach(key -> {
                properties.put(key, env.getProperty(key));
            });
        return properties;
    }
    
    @Value("${kafka.output.topic}")
    String kafka_output_topic = "output";
    
    @Value("${kafka.input.topic}")
    String kafka_input_topic = "input";

    @Autowired
    StreamApplication app;

    @Test
    public void testKafkaStreamTopology() throws IOException {
        
        
        // https://www.programcreek.com/java-api-examples/index.php?api=org.apache.kafka.connect.json.JsonSerializer

        
        // Topology
        Topology topology = app.kafkaStreamTopology();
        
        // setup test driver
        TopologyTestDriver testDriver = new TopologyTestDriver(topology, properties());

        // Setup the message sender
        ConsumerRecordFactory<String, JsonNode> factory = new ConsumerRecordFactory<>(kafka_input_topic, 
                new StringSerializer(),
                new MySerializer());
        JsonNode node = new ObjectMapper().readTree(message());
        ConsumerRecord<byte[], byte[]> message = factory.create(node);
        // Send the message
        testDriver.pipeInput(message);

        // Setup the message consumer
        ProducerRecord<String, JsonNode> outputRecord = testDriver.readOutput(kafka_output_topic, 
                new StringDeserializer(),
                new MyDeserializer());
        
        String output = outputRecord.value().toString();
        
        
        // DEBUG OUTPUTS
        Arrays.asList(
            LINE,
            message(),
            LINE, 
            output.toString(), 
            LINE)
            .forEach(System.out::println);

        testDriver.close();

        assertTrue("Message processed successfully !", 
            output != null && output.toString().length() >0);
        
    }

    private final static String LINE = "=".repeat(80);
}
