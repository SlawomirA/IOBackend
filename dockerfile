FROM openjdk:17-alpine

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} web-server.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/web-server.jar"]