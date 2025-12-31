# Build Instructions

## Prerequisites

Before building the project, ensure you have the following installed:

### Required

- **Git CLI** - For cloning the repository
- **Java SDK 25** - [Download from Oracle](https://www.oracle.com/java/technologies/downloads/#java25) or use [SDKMAN](https://sdkman.io/)
- **Maven 3.9.9** - [Download from Apache Maven](https://maven.apache.org/download.cgi)

### Optional

- **GitHub CLI** - For alternative cloning method
- **Docker** - For containerized builds and deployment

### API Keys

You'll need an API key from one of the following LLM providers (or use Ollama locally):

- [OpenAI API Key](https://platform.openai.com/api-keys)
- [Groq Cloud API Key](https://console.groq.com/keys)
- [Ollama](https://ollama.ai/) - No API key required (self-hosted)

## How to Clone

### Using Git CLI

```bash
git clone https://github.com/pacphi/kahoot-quiz-generator
```

### Using GitHub CLI

```bash
gh repo clone pacphi/kahoot-quiz-generator
```

## How to Build

Navigate to the project directory and execute:

```bash
cd kahoot-quiz-generator
mvn clean package
```

This command will:

1. Clean previous build artifacts
2. Download and install Node.js locally (via frontend-maven-plugin)
3. Install frontend NPM dependencies
4. Build the ReactJS frontend
5. Compile Java backend code
6. Run tests
7. Package everything into a single executable JAR

### Build Output

- Location: `target/kahoot-quiz-generator-{version}.jar`
- Type: Spring Boot executable JAR with embedded frontend

## Build Troubleshooting

### Issue: Missing javax.annotation Package

**Error:**

```bash
package javax.annotation does not exist
```

**Solution:**

Add the annotation-api configuration to the OpenAPI Maven plugin in `pom.xml`.

### Issue: Missing jackson-databind-nullable

**Error:**

```bash
package org.openapitools.jackson.nullable does not exist
```

**Solution:**

Add the following dependencies to `pom.xml`:

```xml
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.1.0</version>
</dependency>
<dependency>
    <groupId>org.openapitools</groupId>
    <artifactId>jackson-databind-nullable</artifactId>
    <version>0.2.6</version>
</dependency>
```

### Issue: OpenAPI Spec Validation Error

**Error:**

```bash
attribute components.securitySchemes.ClientCredentials.scopes is missing
```

**Solution:**

Edit `src/main/resources/openapi/kahoot-openapiv3-spec.yml` and add `scopes: {}` to the ClientCredentials security scheme.

### Issue: Missing API Key During Tests

**Error:**

```bash
OpenAI API key must be set
```

**Solution:**

Create `config/creds.yml` with your API key before running tests. See [RUN.md](RUN.md) for configuration details.

## Skip Tests

If you need to build without running tests:

```bash
mvn clean package -DskipTests
```

## Docker Build

Build and run using Docker (no local Java/Maven required):

```bash
# Build the Docker image
docker build -t kahoot-quiz-generator .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_AI_OPENAI_API_KEY=your-api-key \
  kahoot-quiz-generator

# Access the application
open http://localhost:8080
```

The Dockerfile uses a multi-stage build with:
- **Build stage**: BellSoft Liberica OpenJDK Alpine 25
- **Runtime stage**: BellSoft Liberica JRE Alpine 25 (smaller image)
- Non-root user for security
- Health check on `/actuator/health`

## Verify Installation

Check Java version:

```bash
java -version
# Should show Java 25

mvn -version
# Should show Maven 3.9.9 or later
```

## Dependencies Overview

### Backend Dependencies

- Spring Boot 4.x Starter Web
- Spring AI 2.x (OpenAI/Groq/Ollama integration)
- Apache POI 5.x (Excel generation)
- OpenCSV 5.x (CSV parsing)
- SpringDoc OpenAPI 3.x (API documentation)
- Spring Boot DevTools
- Docker Compose Support
- Micrometer (metrics)
- Resilience4j (fault tolerance)

### Frontend Dependencies

- React 18.x
- Vite 5.x
- Tailwind CSS 3.x
- Radix UI (component primitives)
- Lucide React (icons)
- Vitest (testing)

### Build Plugins

- Spring Boot Maven Plugin
- OpenAPI Generator Maven Plugin
- Frontend Maven Plugin
- CycloneDX Maven Plugin (SBOM generation)
- Git Commit ID Plugin

## Next Steps

After successfully building, see [RUN.md](RUN.md) for instructions on running the application.
