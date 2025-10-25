# Build Instructions

## Prerequisites

Before building the project, ensure you have the following installed:

### Required
- **Git CLI** - For cloning the repository
- **Java SDK 21** - [Download from Oracle](https://www.oracle.com/java/technologies/downloads/#java21) or use [SDKMAN](https://sdkman.io/)
- **Maven 3.9.9** - [Download from Apache Maven](https://maven.apache.org/download.cgi)

### Optional
- **GitHub CLI** - For alternative cloning method

### API Keys
You'll need an API key from one of the following LLM providers:
- [OpenAI API Key](https://platform.openai.com/api-keys)
- [Groq Cloud API Key](https://console.groq.com/keys)

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

## Verify Installation

Check Java version:
```bash
java -version
# Should show Java 21

mvn -version
# Should show Maven 3.9.9 or later
```

## Dependencies Overview

### Backend Dependencies
- Spring Boot Starter Web
- Spring AI (OpenAI/Groq integration)
- Apache POI (Excel generation)
- Spring Boot DevTools
- Docker Compose Support
- Configuration Processor

### Frontend Dependencies
- React 18.x
- Vite 5.x
- Tailwind CSS 3.x
- React DOM

### Build Plugins
- Spring Boot Maven Plugin
- OpenAPI Generator Maven Plugin
- Frontend Maven Plugin

## Next Steps

After successfully building, see [RUN.md](RUN.md) for instructions on running the application.
