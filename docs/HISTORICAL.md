# Historical Migration Notes

This document captures significant migration changes made to the Kahoot Quiz Generator project.

---

## Spring Boot 4.x Migration (December 2025)

### Overview

Migration from Spring Boot 3.5.7 to Spring Boot 4.0.1, including updates to Spring AI 2.0.0-M1.

### Root Cause of Initial Build Failures

After upgrading to Spring Boot 4.0.1, tests failed with:

```text
ClassNotFoundException: org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
```

**Cause**: Spring Boot 4.0 splits the monolithic autoconfigure JAR into modules, causing package relocations. Spring AI 1.1.2 was built for Spring Boot 3.x and referenced old class locations.

**Solution**: Upgrade Spring AI from 1.1.2 to 2.0.0-M1.

---

### Changes to `pom.xml`

#### 1. Spring AI Version Update

```xml
<!-- Before -->
<spring-ai.version>1.1.2</spring-ai.version>

<!-- After -->
<spring-ai.version>2.0.0-M1</spring-ai.version>
```

**Reason**: Spring AI 2.0.0-M1 is the first version built on Spring Boot 4.0 and Spring Framework 7.0 with Jakarta EE 11 baseline.

**Source**: [Spring AI 2.0.0-M1 Release](https://spring.io/blog/2025/12/11/spring-ai-2-0-0-M1-available-now/)

#### 2. Added Spring Milestones Repository

```xml
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

**Reason**: Spring AI 2.0.0-M1 is a milestone release, not yet available in Maven Central GA.

**Source**: [Spring AI Releases](https://github.com/spring-projects/spring-ai/releases)

---

### Changes to `application.yml`

#### 1. TTS Speed Property Type

```yaml
# Before
speed: 1.0f

# After
speed: 1.0
```

**Reason**: Spring AI 2.0 changed the `speed` parameter type from `Float` to `Double`. The `f` suffix is a Java literal, not valid YAML.

**Source**: [Spring AI Upgrade Notes](https://docs.spring.io/spring-ai/reference/upgrade-notes.html)

#### 2. Property Naming Convention (kebab-case)

```yaml
# Before
base_url: ${OPENAI_BASE_URL:https://api.groq.com/openai}

# After
base-url: ${OPENAI_BASE_URL:https://api.groq.com/openai}
```

**Reason**: Spring properties should use kebab-case (dash-separated) per Spring conventions.

**Source**: [Spring AI OpenAI Chat Reference](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html)

#### 3. Model Provider Selection (Ollama Profile)

```yaml
# Before
spring:
  autoconfigure:
    exclude: org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration
  ai:
    ollama:
      ...

# After
spring:
  ai:
    model:
      chat: ollama
      embedding: ollama
    ollama:
      ...
```

**Reason**: In Spring AI 2.0, the monolithic `OpenAiAutoConfiguration` class was split into 6 separate autoconfiguration classes:

- `OpenAiAudioSpeechAutoConfiguration`
- `OpenAiAudioTranscriptionAutoConfiguration`
- `OpenAiChatAutoConfiguration`
- `OpenAiEmbeddingAutoConfiguration`
- `OpenAiImageAutoConfiguration`
- `OpenAiModerationAutoConfiguration`

The new `spring.ai.model.*` property is the recommended way to select/disable providers.

**Source**: [Spring AI Upgrade Notes - Model Auto-Configuration](https://docs.spring.io/spring-ai/reference/upgrade-notes.html)

#### 4. Jackson 3 Logging Package

```yaml
# Before
logging:
  level:
    com.fasterxml.jackson: TRACE

# After
logging:
  level:
    tools.jackson: TRACE
```

**Reason**: Spring Boot 4.x uses Jackson 3 as the default JSON library. Jackson 3 uses new group IDs and package names, with `com.fasterxml.jackson` becoming `tools.jackson`.

**Source**: [Spring Boot 4.0 Migration Guide - Jackson](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)

---

### Key Spring Boot 4.x / Spring Framework 7 Changes

These are notable changes from the migration guides that may affect this project:

| Area | Change | Source |
|------|--------|--------|
| Jackson | Group ID changed from `com.fasterxml.jackson` to `tools.jackson` | [Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) |
| Jackson | `@JsonComponent` renamed to `@JacksonComponent` | [Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) |
| Jackson | Properties `spring.jackson.read.*` and `spring.jackson.write.*` renamed to `spring.jackson.json.read.*` and `spring.jackson.json.write.*` | [Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) |
| Web Starter | `spring-boot-starter-web` renamed to `spring-boot-starter-webmvc` | [Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) |
| Undertow | Support dropped (incompatible with Servlet 6.1) | [Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) |
| Java | Requires Java 17 or later | [Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) |
| Spring AI | Default OpenAI model changed to `gpt-5-mini` | [Spring AI 2.0.0-M1 Release](https://spring.io/blog/2025/12/11/spring-ai-2-0-0-M1-available-now/) |
| Spring AI | Default temperature settings removed; must be explicitly configured | [Spring AI 2.0.0-M1 Release](https://spring.io/blog/2025/12/11/spring-ai-2-0-0-M1-available-now/) |

---

### Important Notes

1. **Spring AI 2.0.0-M1 is a Milestone Release**: As of December 2025, Spring AI 2.0.0 GA has not been released. The GA timing is still being determined by the Spring team.

2. **IDE Warnings**: The IDE may show warnings about `spring.ai.model` being an unknown property. This is because IDE plugins may not have Spring AI 2.0.0-M1 metadata yet. The build works correctly.

3. **Compatibility**: All other Spring Boot 4-compatible projects (Spring Cloud, Spring Modulith, Spring gRPC) have released GA versions except Spring AI.

---

### References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [Spring AI Upgrade Notes](https://docs.spring.io/spring-ai/reference/upgrade-notes.html)
- [Spring AI 2.0.0-M1 Announcement](https://spring.io/blog/2025/12/11/spring-ai-2-0-0-M1-available-now/)
- [Spring AI OpenAI Chat Reference](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html)
- [Spring AI GitHub Releases](https://github.com/spring-projects/spring-ai/releases)
- [Spring AI Boot 4 Compatibility Epic](https://github.com/spring-projects/spring-ai/issues/3379)
- [Spring AI RestClientAutoConfiguration Issue](https://github.com/spring-projects/spring-ai/issues/683)

---

*Last updated: December 30, 2025*
