FROM maven:3.8.1-openjdk-11 as builder
WORKDIR /app
COPY . /app
RUN mvn clean package
FROM openjdk:21-jdk-slim
COPY --from=builder /app/target/GitHubAggregator-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "/app.jar"]