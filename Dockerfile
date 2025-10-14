# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar
COPY --from=builder /workspace/target/sales-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your service is expected to listen on
EXPOSE 8080

# Use a shell command so environment variables and JAVA_OPTS can be expanded
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar /app/app.jar"]