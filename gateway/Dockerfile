FROM openjdk:17-alpine

ARG JAR_FILE=/build/libs/j-gateway.jar

COPY ${JAR_FILE} j-gateway.jar

ENTRYPOINT ["java","-jar","/j-gateway.jar"]