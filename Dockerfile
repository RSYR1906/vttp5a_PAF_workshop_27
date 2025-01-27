# Build stage using Maven and JDK
FROM maven:3.9.9-eclipse-temurin-23 AS compiler

# Define working directory
WORKDIR /code_folder

# Copy Maven configuration and scripts
COPY pom.xml .
COPY mvnw ./mvnw
COPY .mvn .mvn
COPY src src

# Make mvnw executable and build the project
RUN chmod a+x ./mvnw && ./mvnw clean package -Dmaven.test.skip=true

# Runtime stage using OpenJDK
FROM maven:3.9.9-eclipse-temurin-23

WORKDIR /app

# Copy the built jar file
COPY --from=compiler /code_folder/target/Workshop-27-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
ENV PORT=8080
EXPOSE ${PORT}

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]