

Source : https://github.com/spring-cloud/spring-cloud-stream-samples/tree/master/kafka-streams-samples/kafka-streams-word-count


mvn install -DskipTests && docker build -t the-stream:latest .




docker exec kafka kafka-console-consumer --topic kafka-stream1 --from-beginning --zookeeper zookeeper:2181

