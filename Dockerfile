# Stage 1: Build Stage using official Eclipse Temurin JDK 21 Alpine
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy Maven descriptor and source code
COPY pom.xml .
COPY src ./src

# Create classes directory and compile application
RUN mkdir -p target/classes && \
    javac -d target/classes $(find src/main/java -name "*.java") && \
    jar --create --file target/devops-lab-1.0-SNAPSHOT.jar --main-class com.devopslab.App -C target/classes .

# Stage 2: Minimal, Secure Runtime Stage using JRE 21 Alpine
FROM eclipse-temurin:21-jre-alpine AS runner

# Create non-root system group and user for security hardening
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy artifact from builder stage with strict ownership
COPY --from=builder --chown=appuser:appgroup /build/target/devops-lab-1.0-SNAPSHOT.jar app.jar

# Expose standard application HTTP port
EXPOSE 8080

# Configure container healthcheck using curl/wget
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Drop privileges to non-root user
USER appuser

# Environment variables for JVM tuning
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV PORT=8080

# Container entrypoint
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
