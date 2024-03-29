FROM openjdk:17-alpine

ENV POST_SERVER = "http://10.0.1.56:8082" \
    SECRET_KEY = "secret_jjj_project_key_application" \
    TOKEN_DECRYPT_KEY = "project_jj_token" \
    CLIENT_ID = "wuAWZWv8noa7LcgEUPRd" \
    CLIENT_SECRET = "tSBe0L8Ihj" \
    AUTHORIZATION_GRANT_TYPE = "authorization_code" \
    DATASOURCE_URL = "jdbc:postgresql://10.0.1.56:5432/user" \
    DATASOURCE_USERNAME = "postgres" \
    DATASOURCE_PASSWORD = "j1234!@#$"

ARG JAR_FILE=/build/libs/j-user.jar

COPY ${JAR_FILE} j-user.jar

ENTRYPOINT ["java","-jar","/j-user.jar"]