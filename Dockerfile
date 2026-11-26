# -------------------------------------------------------------
# Stage 1: Build the React 19 Frontend
# -------------------------------------------------------------
FROM node:22-alpine AS frontend-builder
WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ ./
RUN npm run build

# -------------------------------------------------------------
# Stage 2: Build the Spring Boot Backend with Embedded UI
# -------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app/backend

COPY backend/pom.xml ./
COPY backend/src ./src
# Embed frontend build into Spring Boot static resources
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static/
RUN mvn clean package -DskipTests

# -------------------------------------------------------------
# Stage 3: Lightweight Production JRE 21 Runtime Container
# -------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=backend-builder /app/backend/target/thapar-journey-connect-backend-1.0.0.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
