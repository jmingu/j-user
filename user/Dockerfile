FROM openjdk:17-alpine

ARG JAR_FILE=/build/libs/j-user.jar

COPY ${JAR_FILE} j-user.jar

ENTRYPOINT ["java","-jar","/j-user.jar"]