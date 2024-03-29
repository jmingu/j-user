FROM openjdk:17-alpine

ENV SECRET_KEY = "secret_jjj_project_key_application" \
    TOKEN_DECRYPT_KEY = "project_jj_token"

ARG JAR_FILE=/build/libs/j-gateway.jar

COPY ${JAR_FILE} j-gateway.jar

ENTRYPOINT ["java","-jar","/j-gateway.jar"]