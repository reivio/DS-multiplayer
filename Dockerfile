FROM openjdk:11

WORKDIR /app

#RUN javac -sourcepath src src/node/Node.java

#CMD ["java", "src/node/Node.java"]

# Add the RabbitMQ Java client library
RUN curl -O https://repo1.maven.org/maven2/com/rabbitmq/amqp-client/5.12.0/amqp-client-5.12.0.jar \
    && curl -O https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.32/slf4j-api-1.7.32.jar \
    && curl -O https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.32/slf4j-simple-1.7.32.jar 
#&& mv amqp-client-5.12.0.jar slf4j-api-1.7.32.jar slf4j-simple-1.7.32.jar /app

COPY . /app
#COPY PublisherSubscriber.java /app

#RUN ./mvnw clean install

#CMD ["bash"]
###RUN javac -cp ".:amqp-client-5.12.0.jar:slf4j-api-1.7.32.jar:slf4j-simple-1.7.32.jar" -sourcepath . src/node/Node.java
RUN javac -cp ".:amqp-client-5.12.0.jar:slf4j-api-1.7.32.jar:slf4j-simple-1.7.32.jar:rabbitmq-http-client-3.8.2.jar" -sourcepath . src/main/java/node/*.java

#CMD ["java", "-cp", ".:amqp-client-5.12.0.jar:slf4j-api-1.7.32.jar:slf4j-simple-1.7.32.jar:src/main/java", "src/main/java/node/Node.java"]
CMD ["java", "-cp", ".:amqp-client-5.12.0.jar:slf4j-api-1.7.32.jar:slf4j-simple-1.7.32.jar:src/main/java", "node/Node"]
