# Running the Application

## Prerequisites

Before running the application, you must:
1. Complete the build process (see [BUILD.md](BUILD.md))
2. Configure your LLM provider API key

## Configuration Setup

### 1. Create Configuration Directory

Create a `config` folder at the project root (sibling to the `src` folder):

```bash
mkdir config
```

### 2. Create Credentials File

Create a file named `creds.yml` inside the `config` folder with your API key:

#### For OpenAI
```yaml
spring:
  ai:
    openai:
      api-key: sk-proj-your-actual-api-key-here
```

#### For Groq Cloud
```yaml
spring:
  ai:
    groq:
      api-key: gsk_your-actual-api-key-here
```

> **Security Note:** Never commit `config/creds.yml` to version control. It should be in `.gitignore`.

## How to Run

### Using OpenAI (Default)

```bash
mvn spring-boot:run
```

The application will start with:
- Spring profile: `openai` and `dev`
- Server port: `8080`
- LLM provider: OpenAI

### Using Groq Cloud

```bash
export CHAT_MODEL=llama-3.3-70b-versatile && \
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=groq-cloud,dev
```

The application will start with:
- Spring profile: `groq-cloud` and `dev`
- Server port: `8080`
- LLM provider: Groq Cloud
- Model: llama-3.3-70b-versatile

## Accessing the Application

Once the application starts successfully, you'll see output like:

```
Started KahootQuizGeneratorApplication in X.XXX seconds
```

### Web Interface
Open your favorite browser and visit:
```
http://localhost:8080
```

You should see the Kahoot Quiz Generator interface where you can:
1. Enter a topic or theme
2. Specify the number of questions
3. Generate and download quiz Excel files

## Shutdown

To stop the application:
- Press `Ctrl+C` in the terminal

## Spring Profiles

### Available Profiles

| Profile | Purpose | Required Config |
|---------|---------|----------------|
| `openai` | Use OpenAI as LLM provider | `spring.ai.openai.api-key` |
| `groq-cloud` | Use Groq Cloud as LLM provider | `spring.ai.groq.api-key` + `CHAT_MODEL` env var |
| `dev` | Development mode with enhanced logging | None |

### Profile Configuration Location

Profiles are configured in `src/main/resources/application.yml`:
- The `default` profile automatically activates `openai` and `dev`
- Override with command-line arguments using `-Dspring-boot.run.arguments`

## Environment Variables

### CHAT_MODEL
Specifies the model to use with Groq Cloud:

```bash
# Llama 3.3 70B
export CHAT_MODEL=llama-3.3-70b-versatile

# Mixtral 8x7B
export CHAT_MODEL=mixtral-8x7b-32768
```

### SPRING_CONFIG_IMPORT
Alternative way to specify credentials file:

```bash
export SPRING_CONFIG_IMPORT=optional:file:./config/creds.yml
mvn spring-boot:run
```

## Troubleshooting

### Issue: API Key Not Found
**Error:**
```
OpenAI API key must be set
```

**Solution:**
1. Verify `config/creds.yml` exists
2. Check the API key is correctly formatted
3. Ensure no extra spaces or quotes around the key

### Issue: Port Already in Use
**Error:**
```
Port 8080 was already in use
```

**Solution:**
Either stop the process using port 8080, or change the port in `application.yml`:
```yaml
server:
  port: 8081
```

### Issue: Groq Cloud Model Not Found
**Error:**
```
Model not found
```

**Solution:**
Verify the `CHAT_MODEL` environment variable matches an available Groq model. See [Groq Models](https://console.groq.com/docs/models) for the latest list.

## Running from JAR

After building, you can also run the application directly from the JAR:

```bash
java -jar target/kahoot-quiz-generator-*.jar
```

With Groq Cloud:
```bash
export CHAT_MODEL=llama-3.3-70b-versatile
java -jar target/kahoot-quiz-generator-*.jar --spring.profiles.active=groq-cloud,dev
```

## Development Mode

For rapid development with auto-restart on code changes:

1. Ensure Spring Boot DevTools is enabled (included by default)
2. Run with `mvn spring-boot:run`
3. Make code changes
4. Application will automatically restart

## Next Steps

- Upload generated Excel files to [Kahoot](https://kahoot.com/)
- See [ARCHITECTURE.md](ARCHITECTURE.md) for technical details
- See [CI.md](CI.md) for continuous integration setup
