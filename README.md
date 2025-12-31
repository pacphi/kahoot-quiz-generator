# Kahoot Quiz Generator

[![GA](https://img.shields.io/badge/Release-GA-darkgeen)](https://img.shields.io/badge/Release-Alpha-darkred) ![Github Action CI Workflow Status](https://github.com/pacphi/kahoot-quiz-generator/actions/workflows/ci.yml/badge.svg) [![Known Vulnerabilities](https://snyk.io/test/github/pacphi/kahoot-quiz-generator/badge.svg?style=plastic)](https://snyk.io/test/github/pacphi/kahoot-quiz-generator)

An AI-powered application that generates Kahoot quiz questions using LLMs (OpenAI or Groq Cloud). Create themed quizzes with multiple-choice questions and export them as Excel files ready for upload to [Kahoot](https://kahoot.com/).

## Quick Links

- **[User Guide](docs/USER.md)** - How to generate quizzes and upload to Kahoot
- [Backstory](docs/BACKSTORY.md) - Project origin, prompt history, and development journey
- [Architecture](docs/ARCHITECTURE.md) - Technical design and technology stack
- [Build Instructions](docs/BUILD.md) - How to clone and build the project
- [Run Instructions](docs/RUN.md) - How to configure and run the application
- [Deployment](docs/DEPLOY.md) - Deploy to Fly.io with scale-to-zero configuration
- [CI/CD](docs/CI.md) - Continuous integration and security scanning
- [Brief Demonstration](https://github.com/pacphi/kahoot-quiz-generator/raw/refs/heads/main/kahoot-quiz-generator.mp4)

## Features

- **Dual-Mode Interface**: AI-powered generation or CSV import with tab-based UI
- **Multiple LLM Providers**: OpenAI, Groq Cloud, or Ollama (local models)
- **CSV Import with Validation**: Intelligent column detection, real-time preview, comprehensive error reporting
- **Quiz Customization**: Shuffle questions, randomize answer positions, custom time limits (5-240s)
- **Kahoot-Ready Export**: Excel files conforming to official Kahoot template format
- **Docker Support**: Multi-stage Dockerfile with health checks and non-root user
- **Observability**: Prometheus metrics, health endpoints, OpenTelemetry tracing, SBOM generation
- **Virtual Threads**: Java 25 with virtual threads for improved performance
- **API Documentation**: Swagger UI and OpenAPI 3.0 spec included

## What It Does

This application:

- Accepts a topic/theme and number of questions as input (AI mode)
- Imports existing questions from CSV files with validation (CSV mode)
- Generates quiz questions using AI (OpenAI, Groq Cloud, or Ollama)
- Creates 4 multiple-choice answers per question (one correct answer)
- Exports results as an Excel (.xlsx) file conforming to [Kahoot's Quiz template](https://kahoot.com/files/2019/08/Kahoot-Quiz-Spreadsheet-Template.xlsx)
- Provides a clean ReactJS interface with real-time preview and error handling

## Quick Start

### Prerequisites

- Java SDK 25
- Maven 3.9.9
- OpenAI or Groq Cloud API key

### Clone

```bash
git clone https://github.com/pacphi/kahoot-quiz-generator
cd kahoot-quiz-generator
```

### Build

```bash
mvn clean package
```

### Configure

Create `config/creds.yml`:

```yaml
spring:
  ai:
    openai:
      api-key: your-api-key-here
```

### Run

```bash
mvn spring-boot:run
```

### Run with Docker

```bash
docker build -t kahoot-quiz-generator .
docker run -p 8080:8080 -e SPRING_AI_OPENAI_API_KEY=your-key kahoot-quiz-generator
```

### Use

Open your browser to `http://localhost:8080`

- **Application**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/actuator/health`

## Tech Stack

- **Backend:** Spring Boot 4.x, Spring AI 2.x, Apache POI, OpenCSV, Java 25
- **Frontend:** ReactJS 18, Vite 5, Tailwind CSS 3, Radix UI
- **LLM Providers:** OpenAI (GPT-4o-mini), Groq Cloud (LLaMA 3.3-70B), Ollama (local)
- **Build:** Maven with frontend-maven-plugin, CycloneDX for SBOM
- **Observability:** Micrometer, Prometheus, OpenTelemetry, Spring Boot Actuator
- **Documentation:** SpringDoc OpenAPI 3.0, Swagger UI

## Documentation

For detailed information, see:

- **[USER.md](docs/USER.md)** - Complete user guide for generating quizzes and uploading to Kahoot
- **[BACKSTORY.md](docs/BACKSTORY.md)** - How this project came to be, including all the prompts used with Claude AI and the iterative development process
- **[ARCHITECTURE.md](docs/ARCHITECTURE.md)** - System design, technology choices, and Spring profiles
- **[BUILD.md](docs/BUILD.md)** - Complete build instructions and troubleshooting
- **[RUN.md](docs/RUN.md)** - Configuration, running, and environment variables
- **[DEPLOY.md](docs/DEPLOY.md)** - Secure deployment to Fly.io with scale-to-zero configuration
- **[CI.md](docs/CI.md)** - GitHub Actions, testing, and security scanning

## License

See LICENSE file for details.

## Contributing

Contributions welcome! Please read the documentation before submitting PRs.
