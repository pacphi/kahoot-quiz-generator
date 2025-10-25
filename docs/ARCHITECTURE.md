# Architecture

## Technology Stack

### Backend
- **Java 21** - Modern Java LTS release
- **Spring Boot 3.x** - Application framework
- **Spring AI** - LLM integration with support for multiple providers
- **Apache POI** - Excel (.xlsx) file generation
- **Maven 3.9.9** - Build and dependency management

### Frontend
- **ReactJS** - UI framework
- **Vite** - Build tool and development server
- **Tailwind CSS** - Utility-first CSS framework
- **PostCSS** - CSS processing

### LLM Providers
- **OpenAI** - Primary LLM provider
- **Groq Cloud** - Alternative LLM provider (llama-3.3-70b-versatile)

## Frontend Structure

The frontend is built with ReactJS and managed through the `frontend-maven-plugin` for seamless integration with the Maven build process.

```
src/main/frontend/
├── index.html              # Entry HTML file
├── package.json            # NPM dependencies
├── postcss.config.js       # PostCSS configuration
├── tailwind.config.js      # Tailwind CSS configuration
├── vite.config.js          # Vite build configuration
└── src/
    ├── App.jsx             # Main application component
    ├── index.css           # Global styles
    ├── main.jsx            # Application entry point
    └── components/
        ├── QuizForm.jsx    # Quiz generation form
        └── ui/
            ├── alert.jsx   # Alert component
            └── card.jsx    # Card component
```

## Backend Structure

The backend follows a clean Spring Boot architecture with service-oriented design:

```
src/main/java/me/pacphi/kahoot/
├── controller/             # REST API endpoints
├── service/
│   └── KahootService.java  # Core business logic for quiz generation
├── model/
│   └── KahootQuestion.java # Domain model for quiz questions
└── config/                 # Spring configuration
```

### Key Components

#### KahootService
The core service responsible for:
- Generating quiz questions using Spring AI's ChatClient
- Interacting with LLM providers (OpenAI/Groq)
- Creating Excel files conforming to Kahoot's template format
- Validating question structure (4 choices, 1 correct answer)

#### KahootQuestion Record
```java
public record KahootQuestion(
    String question,
    List<Choice> choices) {

    public record Choice(
        String answerText,
        boolean isCorrect) {
    }
}
```

Validates:
- Non-empty question text
- Exactly 4 choices
- Exactly one correct answer

## Spring Profiles

The application uses Spring profiles for flexible configuration:

### Default Profile
- Automatically activates `openai` and `dev` profiles
- Configured in `src/main/resources/application.yml`

### Available Profiles

#### `openai`
- Uses OpenAI as the LLM provider
- Requires `spring.ai.openai.api-key` in `config/creds.yml`

#### `groq-cloud`
- Uses Groq Cloud as the LLM provider
- Requires `spring.ai.groq.api-key` in `config/creds.yml`
- Requires `CHAT_MODEL` environment variable (e.g., `llama-3.3-70b-versatile`)

#### `dev`
- Development-specific configurations
- Enhanced logging and debugging

### Switching Profiles

```bash
# Use Groq Cloud instead of OpenAI
export CHAT_MODEL=llama-3.3-70b-versatile && \
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=groq-cloud,dev
```

## Build Integration

The `frontend-maven-plugin` integrates the ReactJS frontend into the Maven build lifecycle:

1. **Install Node/NPM** - Downloads and installs Node.js locally
2. **Install Dependencies** - Runs `npm install`
3. **Build Frontend** - Runs `npm run build`
4. **Package** - Frontend build output is included in the Spring Boot JAR

This allows a single `mvn package` command to build both frontend and backend.

## API Design

The application exposes REST endpoints for:
- Generating quiz questions from prompts
- Downloading generated Excel files
- Health checks and status

All responses conform to standard HTTP status codes and include appropriate error handling.
