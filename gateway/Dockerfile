FROM openjdk:17-alpine

ENV ALLOWED_ORIGINS = "https://project-j.co.kr" \
    USER_SERVER = "http://10.0.1.56:8081" \
    POST_SERVER = "http://10.0.1.56:8082"

ARG JAR_FILE=/build/libs/j-gateway.jar

COPY ${JAR_FILE} j-gateway.jar

ENTRYPOINT ["java","-jar","/j-gateway.jar"]