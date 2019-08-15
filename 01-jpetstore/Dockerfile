FROM maven

RUN git clone https://github.com/mybatis/jpetstore-6.git && cd jpetstore-6 && pwd && mvn clean package -DskipTests
EXPOSE 8080

CMD cd /jpetstore-6 && mvn cargo:run -P tomcat90
    