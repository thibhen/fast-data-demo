# Getting Started

### Run with Maven

mvn spring-boot:run

### Build docker image
mvn install
docker build -t the-proxy:latest .


## Navigate
http://localhost:8083/jpetstore

# Check KAFKA Console
docker exec kafka kafka-console-consumer --topic raw-http --from-beginning --zookeeper zookeeper:2181