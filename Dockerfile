FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY server/build/libs/kafka-web-ui-1.0.0.jar app.jar
EXPOSE 8080
#ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dspring.config.location=$CONFIG_LOCATION -jar /app.jar
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dspring.config.location=/Users/rahul.bhattacharya/certs/application.properties -jar /app.jar
