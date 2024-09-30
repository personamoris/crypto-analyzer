# Build stage
FROM maven:3.8.4-openjdk-17 AS build  # Use the official Maven image with OpenJDK 17 to build the project
WORKDIR /app                          # Set the working directory inside the container to /app
COPY pom.xml .                        # Copy the pom.xml file into the container to resolve dependencies
COPY src ./src                        # Copy the entire source code directory into the container
RUN mvn clean package -DskipTests     # Run the Maven command to clean and package the project, skipping tests

# Run stage
FROM openjdk:17-jdk-slim              # Use a slim version of the OpenJDK 17 image for running the application
WORKDIR /app                          # Set the working directory inside the container to /app
COPY --from=build /app/target/crypto-analyzer-0.0.1-SNAPSHOT.jar app.jar  # Copy the built JAR from the build stage
EXPOSE 8080                           # Expose port 8080 for the application
ENTRYPOINT ["java", "-jar", "app.jar"] # Set the command to run the application JAR