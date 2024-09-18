# Stage 1: Build the JAR file
FROM eclipse-temurin:21-jdk-alpine as builder

# Set the working directory
WORKDIR /app

# Copy Gradle wrapper and project files
COPY gradle ./gradle
COPY gradlew ./
COPY build.gradle .
COPY settings.gradle .

# Copy application source code
COPY src ./src

# Ensure Gradle wrapper is executable
RUN chmod +x ./gradlew

# Build the JAR file for the main application (API)
RUN ./gradlew bootJar --no-daemon -Pprofile=app

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port for the API
EXPOSE 8080

# Set the entry point to run the API with the app profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=app"]
