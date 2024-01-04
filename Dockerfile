FROM alpine:3.19

# Install Java 21
RUN apk update && \
    apk add openjdk21-jre && \
    rm -rf /var/cache/apk/*;

# Path to .jar
ARG JAR_FILE=./target/scala-3.3.1/2vs-startgg-service.jar

COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]