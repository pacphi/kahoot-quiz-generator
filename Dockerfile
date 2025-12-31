# Build stage
FROM bellsoft/liberica-openjdk-alpine:25 AS builder

# Install C++ runtime libraries required by Node.js on Alpine/musl
RUN apk add --no-cache libstdc++ libgcc

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application (use fly profile to skip git-commit-id plugin)
RUN ./mvnw clean package -DskipTests -Pfly

# Extract Spring Boot layers for better caching
RUN mkdir -p target/dependency && \
    cd target/dependency && \
    jar -xf ../*.jar

# Runtime stage
FROM bellsoft/liberica-openjre-alpine:25

# Create non-root user for security
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser

WORKDIR /app

# Copy Spring Boot layers from builder stage
COPY --from=builder /app/target/dependency/BOOT-INF/lib ./lib
COPY --from=builder /app/target/dependency/META-INF ./META-INF
COPY --from=builder /app/target/dependency/BOOT-INF/classes ./

# Set ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check (using wget since Alpine doesn't have curl by default)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-cp", "./:./lib/*", "me.pacphi.kahoot.KahootQuizGeneratorApplication"]
