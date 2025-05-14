FROM openjdk:17-jdk-alpine
WORKDIR /app
ARG JAR_FILE=target/review_serivce-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
