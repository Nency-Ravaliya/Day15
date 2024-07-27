# Stage 1: Build the JAR file
FROM maven:3.9.2-eclipse-temurin-17 AS build

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the pom.xml and src directory to the container
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /usr/app

# Copy the JAR file from the build stage
COPY --from=build /usr/src/app/target/my-app-1.0-SNAPSHOT.jar ./my-app.jar

# Specify the command to run the JAR file
ENTRYPOINT ["java", "-jar", "/usr/app/my-app.jar"]
