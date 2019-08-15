
Build the container

    docker build -t jpetstore .


Start The container

    docker-compose up






JPETSTORE
======================


    docker run -p 8080:8080 -ti maven -name jpetstore /bin/bash
    git clone https://github.com/mybatis/jpetstore-6.git
    cd jpetstore-6/
    ./mvnw clean package
    ./mvnw cargo:run -P tomcat90

    http://localhost:8080/jpetstore