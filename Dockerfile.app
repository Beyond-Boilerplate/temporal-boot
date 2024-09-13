FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradle ./gradle
COPY gradlew ./
COPY build.gradle ./
COPY settings.gradle ./

# Download dependencies
RUN ./gradlew build -x test --no-daemon

# Copy the source code
COPY src ./src

# Build the application JAR
RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built app JAR
COPY --from=builder /app/build/libs/*-app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
