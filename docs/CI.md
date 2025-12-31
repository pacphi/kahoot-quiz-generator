# Continuous Integration

## Overview

This project uses GitHub Actions for continuous integration and security scanning to ensure code quality and identify vulnerabilities.

## CI Workflow

### Build Status

![Github Action CI Workflow Status](https://github.com/pacphi/kahoot-quiz-generator/actions/workflows/ci.yml/badge.svg)

### Workflow Configuration

The CI workflow is defined in `.github/workflows/ci.yml` and runs on:

- **Trigger:** Push to any branch, Pull requests
- **Runner:** Ubuntu latest
- **Java Version:** 25
- **Java Distribution:** Liberica
- **Maven Version:** 3.9.9

### CI Pipeline Steps

1. **Checkout Code**
   - Fetches the latest code from the repository

2. **Setup Java**
   - Installs Java SDK 21
   - Configures Maven cache for faster builds

3. **Build Project**
   - Executes `mvn clean package`
   - Compiles backend Java code
   - Builds frontend ReactJS application
   - Runs unit tests
   - Generates executable JAR

4. **Run Tests**
   - Executes all test suites
   - Generates test reports
   - Enforces code coverage requirements

5. **Archive Artifacts**
   - Stores build artifacts for download
   - Preserves JAR file for deployment

## Security Scanning

### Snyk Integration

[![Known Vulnerabilities](https://snyk.io/test/github/pacphi/kahoot-quiz-generator/badge.svg?style=plastic)](https://snyk.io/test/github/pacphi/kahoot-quiz-generator)

The project uses [Snyk](https://snyk.io) for continuous security monitoring:

#### What Snyk Checks

- **Dependency Vulnerabilities:** Scans all Maven and NPM dependencies
- **License Compliance:** Identifies licensing issues
- **Code Security:** Detects potential security issues in code
- **Container Scanning:** Checks Docker images for vulnerabilities

### SBOM (Software Bill of Materials)

The project generates a CycloneDX SBOM during the build process:

- **Format:** CycloneDX JSON
- **Generation:** Automatic via `cyclonedx-maven-plugin`
- **Access:** Available at `/actuator/sbom` endpoint when running
- **Benefits:** Complete inventory of dependencies for security auditing

#### Snyk Configuration

- **Frequency:** Automatic scans on every push and weekly scheduled scans
- **Severity Threshold:** Alerts on medium and high severity issues
- **Auto-fix:** Snyk can automatically create PRs to fix vulnerabilities

#### Viewing Snyk Results

1. Click the Snyk badge in the README
2. View detailed vulnerability reports
3. See recommended fixes and patches

## Testing Strategy

### Unit Tests

- **Framework:** JUnit 5
- **Coverage Goal:** 80%+
- **Location:** `src/test/java`

### Integration Tests

- Tests Spring Boot application context
- Validates REST API endpoints
- Verifies LLM integration

### Frontend Tests

- **Framework:** Vitest (if configured)
- **Location:** `src/main/frontend/src/__tests__`

## Build Artifacts

After successful CI builds, the following artifacts are available:

- **JAR File:** `kahoot-quiz-generator-{version}.jar`
- **Test Reports:** JUnit XML format
- **Coverage Reports:** JaCoCo format (if configured)

## Release Process

### GA Release

![Release Status](https://img.shields.io/badge/Release-GA-darkgreen)

The project is currently in **GA (General Availability)** status, indicating:

- Stable release
- Production-ready
- API is stable
- Regular maintenance and updates

### Future Release Workflow

1. **Version Bump:** Update version in `pom.xml`
2. **Tag Release:** Create Git tag (e.g., `v1.0.0`)
3. **GitHub Release:** Automated release creation
4. **Artifact Upload:** JAR file attached to release

## Local CI Simulation

To simulate CI builds locally:

```bash
# Run the full build like CI does
mvn clean package

# Run only tests
mvn test

# Run with coverage
mvn clean test jacoco:report

# Skip tests (not recommended)
mvn clean package -DskipTests
```

## CI Environment Variables

The CI workflow uses the following secrets and variables:

### Secrets (Configured in GitHub)

- `OPENAI_API_KEY` - OpenAI API key for test execution
- `SNYK_TOKEN` - Snyk authentication token

### Environment Variables

- `JAVA_VERSION` - Java SDK version (25)
- `MAVEN_OPTS` - Maven memory settings

## Troubleshooting CI Failures

### Build Failures

1. Check the GitHub Actions logs
2. Look for compilation errors
3. Verify all dependencies are available
4. Check for test failures

### Test Failures

1. Review test logs in GitHub Actions
2. Run tests locally: `mvn test`
3. Check for environment-specific issues
4. Verify API keys are configured correctly

### Security Scan Failures

1. Review Snyk report details
2. Check severity of vulnerabilities
3. Update vulnerable dependencies
4. Apply recommended patches

## Contributing

When contributing, ensure:

1. All CI checks pass
2. No new security vulnerabilities introduced
3. Tests are added for new features
4. Code coverage remains above 80%

## Monitoring

### Build Health

- Monitor build status in GitHub Actions
- Review build duration trends
- Track test success rates

### Security Health

- Weekly Snyk scans
- Immediate alerts for high-severity issues
- Dependency update notifications

## Next Steps

- Configure code coverage reporting
- Add performance benchmarks
- Implement automated deployment
- Add API documentation generation
