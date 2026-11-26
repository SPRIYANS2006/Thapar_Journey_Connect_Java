# -------------------------------------------------------------
# Stage 1: Build the Spring Boot Backend with Embedded UI
# -------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app/backend

# Copy backend source (including static UI assets) and Maven POM
COPY backend/pom.xml ./
COPY backend/src ./src

# Build production JAR package skipping unit tests for fast deploy
RUN mvn clean package -DskipTests

# -------------------------------------------------------------
# Stage 2: Ultra-lightweight Production JRE 21 Runtime Container
# -------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built JAR from builder stage
COPY --from=backend-builder /app/backend/target/thapar-journey-connect-backend-1.0.0.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
