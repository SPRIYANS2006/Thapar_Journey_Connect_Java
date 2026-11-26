# -------------------------------------------------------------
# Production Lightweight JRE 21 Runtime Container
# -------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy pre-packaged production JAR (with embedded React 19 UI)
COPY backend/app.jar app.jar

ENV PORT=8080
EXPOSE 8080

# Configure JVM heap limits suitable for Render's 512MB RAM free tier
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Xmx384m", "-jar", "app.jar"]
