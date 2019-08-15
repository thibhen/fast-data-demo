
Create the KAFKA Cluster 
========================

    Ref : https://hub.docker.com/r/confluent/kafka/


    docker-compose up

    OR

        # Start Zookeeper and expose port 2181 for use by the host machine
        docker run -d --name zookeeper -p 2181:2181 confluent/zookeeper

        # Start Kafka and expose port 9092 for use by the host machine
        docker run -d --name kafka -p 9092:9092 --link zookeeper:zookeeper confluent/kafka

        # Start Schema Registry and expose port 8081 for use by the host machine
        docker run -d --name schema-registry -p 8081:8081 --link zookeeper:zookeeper \
            --link kafka:kafka confluent/schema-registry

        # Start REST Proxy and expose port 8082 for use by the host machine
        docker run -d --name rest-proxy -p 8082:8082 --link zookeeper:zookeeper \
            --link kafka:kafka --link schema-registry:schema-registry confluent/rest-proxy


Check Status of the Cluster
============================

    thibaut@theserver:~/dev/fast-data-demo$ docker ps
    CONTAINER ID        IMAGE                       COMMAND                  CREATED              STATUS              PORTS                                        NAMES
    33e25d22a8ea        confluent/rest-proxy        "/usr/local/bin/re..."   9 seconds ago        Up 9 seconds        0.0.0.0:8082->8082/tcp                       rest-proxy
    7fadde77e01a        confluent/schema-registry   "/usr/local/bin/sc..."   23 seconds ago       Up 22 seconds       0.0.0.0:8081->8081/tcp                       schema-registry
    2f48720ef175        confluent/kafka             "/usr/local/bin/ka..."   36 seconds ago       Up 35 seconds       0.0.0.0:9092->9092/tcp                       kafka
    2101624f3b16        confluent/zookeeper         "/usr/local/bin/zk..."   About a minute ago   Up About a minute   2888/tcp, 0.0.0.0:2181->2181/tcp, 3888/tcp   zookeeper


Start the Cluster
============================

    docker start zookeeper 
    docker start kafka
    docker start schema-registry 
    docker start rest-proxy


Send/receive messages in the Cluster
====================================

Start a console :
     docker exec -ti kafka /bin/bash

List the topics
    kafka-topics --zookeeper zookeeper:2181 --list

Create a test topic
    confluent@2f48720ef175:/$ kafka-topics --zookeeper zookeeper:2181 --create --topic test --partitions 1 --replication-factor 1
    Created topic "test".

Send Messages
    kafka-console-producer --topic test --broker-list localhost:9092

Read Messages
    kafka-console-consumer --topic test --from-beginning --zookeeper zookeeper:2181


KAFKA REST
==========

     curl "http://localhost:8082/topics"


    Produce a message
    curl -X POST -H "Content-Type: application/vnd.kafka.json.v2+json" \
          --data '{"records":[{"value":{"name": "testUser"}}]}' \
          "http://localhost:8082/topics/jsontest"