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

## What It Does

This application:

- Accepts a topic/theme and number of questions as input
- Generates quiz questions using AI (OpenAI or Groq Cloud)
- Creates 4 multiple-choice answers per question (one correct answer)
- Exports results as an Excel (.xlsx) file conforming to [Kahoot's Quiz template](https://kahoot.com/files/2019/08/Kahoot-Quiz-Spreadsheet-Template.xlsx)
- Provides a clean ReactJS interface for easy quiz generation

## Quick Start

### Prerequisites

- Java SDK 21
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

### Use

Open your browser to `http://localhost:8080`

## Tech Stack

- **Backend:** Spring Boot 3.x, Spring AI, Apache POI, Java 21
- **Frontend:** ReactJS, Vite, Tailwind CSS
- **LLM Providers:** OpenAI, Groq Cloud
- **Build:** Maven with frontend-maven-plugin

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
