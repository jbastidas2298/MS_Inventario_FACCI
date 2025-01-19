FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ROOT.jar app.jar
EXPOSE 8099
ENTRYPOINT ["java", "-jar", "app.jar"]
