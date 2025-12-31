# Architecture

## Technology Stack

### Backend

- **Java 25** - Latest Java release with virtual threads support
- **Spring Boot 4.x** - Application framework
- **Spring AI 2.x** - LLM integration with support for multiple providers
- **Apache POI 5.x** - Excel (.xlsx) file generation
- **OpenCSV 5.x** - CSV file parsing and validation
- **Maven 3.9.9** - Build and dependency management

### Frontend

- **ReactJS 18** - UI framework with hooks
- **Vite 5** - Build tool and development server
- **Tailwind CSS 3** - Utility-first CSS framework
- **Radix UI** - Accessible component primitives
- **Lucide React** - Icon library
- **PostCSS** - CSS processing

### LLM Providers

- **OpenAI** - Primary LLM provider (GPT-4o-mini default)
- **Groq Cloud** - Alternative LLM provider (llama-3.3-70b-versatile)
- **Ollama** - Local LLM provider for self-hosted models

### Observability

- **Spring Boot Actuator** - Health, metrics, and info endpoints
- **Micrometer** - Metrics facade
- **Prometheus** - Metrics endpoint (`/actuator/prometheus`)
- **OpenTelemetry** - Distributed tracing support
- **CycloneDX** - SBOM generation (`/actuator/sbom`)

## Frontend Structure

The frontend is built with ReactJS and managed through the `frontend-maven-plugin` for seamless integration with the Maven build process.

```text
src/main/frontend/
├── index.html              # Entry HTML file
├── package.json            # NPM dependencies
├── postcss.config.js       # PostCSS configuration
├── tailwind.config.js      # Tailwind CSS configuration
├── vite.config.js          # Vite build configuration
└── src/
    ├── App.jsx             # Main application with tab navigation
    ├── index.css           # Global styles
    ├── main.jsx            # Application entry point
    └── components/
        ├── AiGenerationTab.jsx    # AI quiz generation interface
        ├── CsvUploadTab.jsx       # CSV file upload and preview
        ├── QuizForm.jsx           # Quiz generation form
        ├── CsvPreview.jsx         # CSV data preview with validation
        ├── ConversionOptions.jsx  # Shuffle/time limit options
        └── ui/
            ├── alert.jsx          # Alert component
            ├── button.jsx         # Button component
            ├── card.jsx           # Card component
            ├── input.jsx          # Input component
            └── tabs.jsx           # Tab navigation (Radix UI)
```

## Backend Structure

The backend follows a clean Spring Boot architecture with service-oriented design:

```text
src/main/java/me/pacphi/kahoot/
├── KahootQuizGeneratorApplication.java  # Main application class
├── service/
│   ├── KahootQuizGeneratorController.java  # REST API endpoints
│   ├── KahootService.java      # AI quiz generation service
│   ├── ExcelService.java       # Excel file generation
│   ├── CsvParserService.java   # CSV parsing and validation
│   └── KahootQuestion.java     # Domain model for quiz questions
└── model/
    ├── CsvConversionRequest.java   # CSV conversion request DTO
    ├── CsvPreviewResponse.java     # CSV preview response with validation
    └── ValidationError.java        # Validation error model
```

### Key Components

#### KahootService

The AI service responsible for:
- Generating quiz questions using Spring AI's ChatClient
- Interacting with LLM providers (OpenAI/Groq/Ollama)
- Validating question structure (4 choices, 1 correct answer)

#### ExcelService

Excel generation service responsible for:
- Creating Excel files conforming to Kahoot's template format
- Shuffling questions and answer positions (optional)
- Applying custom time limits per question

#### CsvParserService

CSV processing service responsible for:
- Parsing CSV files with intelligent column detection
- Supporting multiple column name aliases (case-insensitive)
- Validating questions with detailed error reporting
- Enforcing limits (max 100 questions, 5MB file size)

#### KahootQuestion Record

```java
public record KahootQuestion(
    String question,
    List<Choice> choices,
    int timeLimit) {

    public record Choice(
        String answerText,
        boolean isCorrect) {
    }
}
```

Validates:
- Non-empty question text
- Exactly 4 choices with non-empty text
- Exactly one correct answer
- Time limit between 5-240 seconds

## Spring Profiles

The application uses Spring profiles for flexible configuration:

### Default Profile

- Automatically activates `openai` and `dev` profiles
- Configured in `src/main/resources/application.yml`

### Available Profiles

#### `openai`

- Uses OpenAI as the LLM provider
- Requires `spring.ai.openai.api-key` in `config/creds.yml`
- Default model: `gpt-4o-mini`

#### `groq-cloud`

- Uses Groq Cloud as the LLM provider
- Requires `spring.ai.groq.api-key` in `config/creds.yml`
- Requires `CHAT_MODEL` environment variable (e.g., `llama-3.3-70b-versatile`)

#### `ollama`

- Uses Ollama for local LLM inference
- No API key required (self-hosted)
- Requires Ollama running locally (default: `http://localhost:11434`)
- Configure model via `CHAT_MODEL` environment variable

#### `docker`

- Docker Compose integration for local development
- Auto-configures service connections

#### `dev`

- Development-specific configurations
- Enhanced logging and debugging
- Full actuator endpoint exposure

### Switching Profiles

```bash
# Use Groq Cloud instead of OpenAI
export CHAT_MODEL=llama-3.3-70b-versatile && \
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=groq-cloud,dev

# Use Ollama with local Mistral model
export CHAT_MODEL=mistral && \
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=ollama,dev
```

## Build Integration

The `frontend-maven-plugin` integrates the ReactJS frontend into the Maven build lifecycle:

1. **Install Node/NPM** - Downloads and installs Node.js locally
2. **Install Dependencies** - Runs `npm install`
3. **Build Frontend** - Runs `npm run build`
4. **Package** - Frontend build output is included in the Spring Boot JAR

This allows a single `mvn package` command to build both frontend and backend.

## API Design

### REST Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/quiz/generate` | POST | Generate quiz using AI from topic |
| `/api/quiz/upload` | POST | Upload and validate CSV file |
| `/api/quiz/convert` | POST | Convert validated CSV to Excel |

### Management Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health status |
| `/actuator/info` | Build and git information |
| `/actuator/metrics` | Micrometer metrics |
| `/actuator/prometheus` | Prometheus-compatible metrics |
| `/actuator/sbom` | Software Bill of Materials (CycloneDX) |
| `/swagger-ui.html` | Interactive API documentation |
| `/v3/api-docs` | OpenAPI 3.0 specification |

All responses conform to standard HTTP status codes and include appropriate error handling.
