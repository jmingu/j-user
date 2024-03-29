FROM openjdk:17-alpine

ENV ALLOWED_ORIGINS = "https://project-j.co.kr" \
    USER_SERVER = "http://10.0.1.56:8081" \
    POST_SERVER = "http://10.0.1.56:8082" \
    SECRET_KEY = "secret_jjj_project_key_application" \
    TOKEN_DECRYPT_KEY = "project_jj_token" \
    CLIENT_ID = "wuAWZWv8noa7LcgEUPRd" \
    CLIENT_SECRET = "tSBe0L8Ihj" \
    AUTHORIZATION_GRANT_TYPE = "authorization_code" \
#    60분
    EXPIRED_TIME = 60 \
#    7일
    EXPIRED_REFRESH_TIME = 10080 \
    DATASOURCE_URL = "jdbc:postgresql://10.0.1.56:5432/user" \
    DATASOURCE_USERNAME = "postgres" \
    DATASOURCE_PASSWORD = "j1234!@#$"

ARG JAR_FILE=/build/libs/j-gateway.jar

COPY ${JAR_FILE} j-gateway.jar

ENTRYPOINT ["java","-jar","/j-gateway.jar"]