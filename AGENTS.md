# AGENTS.md

Instructions for coding agents working in this repo. `README.md` links the guides; `docs/BUILD.md`, `docs/RUN.md`,
`docs/ARCHITECTURE.md`, `docs/CI.md` and `docs/DEPLOY.md` have the detail. This file holds the rules.

## What this is

Kahoot Quiz Generator: a Spring Boot app with a React front end. It asks an LLM (OpenAI, Groq Cloud or Ollama) for
multiple-choice questions, or imports them from CSV, and exports an Excel file in Kahoot's quiz template format. One
executable JAR serves the API and the built front end.

- `src/main/java/me/pacphi/kahoot/` the backend: `service/` (the REST controller, `KahootService` for LLM generation,
  `ExcelService`, `CsvParserService`) and `model/` (request and response types).
- `src/main/frontend/` the React app (Vite, Tailwind, Radix). Maven builds it and copies `dist/` into the JAR.
- `src/main/resources/` `application.yml` (profiles `openai`, `groq-cloud`, `ollama`, `docker`, `dev`), the OpenAPI spec in
  `openapi/kahoot-openapiv3-spec.yml`, and the Kahoot spreadsheet template.
- `src/test/java/` JUnit tests; `src/main/frontend/src/test/` Vitest setup.

## Toolchain

- Java 25 (Liberica in CI), Spring Boot 4, Spring AI 2. Use the wrapper: `./mvnw`. Versions are properties in `pom.xml`.
- The front end builds through `frontend-maven-plugin`, which installs its own Node and npm (`node.version` and
  `npm.version` in `pom.xml`). Do not rely on a machine Node for the build.
- The front end uses caret ranges, and `package-lock.json` is gitignored, so front-end installs are not reproducible. Do not
  commit a lockfile or change that without the owner's say.

## Before you push

Run `./mvnw clean package`. It is what CI runs (Java 25, on every push): front-end build, backend compile, Java tests and
the JAR. It does not run the front-end tests, so also run `npm test` (Vitest) in `src/main/frontend` when you touch it.

## Rules that are easy to break

- **Keep the Kahoot export valid.** Each question has 4 choices and exactly 1 correct answer. The default time limit is
  5 to 240 seconds. The workbook must keep matching the template in `src/main/resources/templates/`.
- **The API starts from the OpenAPI spec.** `openapi-generator-maven-plugin` generates Spring interfaces from
  `src/main/resources/openapi/kahoot-openapiv3-spec.yml` at build time. It is limited to `ApiUtil.java` as the only
  supporting file, because a full pass writes a stray `pom.xml` that Snyk flags as a separate project (#110). Keep that limit
  and do not commit generated sources.
- **Secrets stay out of the repo.** API keys go in `config/creds.yml` (gitignored by `config/creds*.yml`) or environment
  variables such as `SPRING_AI_OPENAI_API_KEY`. Never commit a key, and never paste one into docs or tests.
- **Provider choice is a profile**, not code. Add or change a provider in `application.yml` and the Spring AI dependencies,
  and keep OpenAI as the default.
- **Do not deploy.** `fly-deploy.yml` is manual (`workflow_dispatch`) and needs `FLY_API_TOKEN`. Deploy only when asked.
- **Keep `.snykignore` and the `target/` exclusion** so build output is not scanned as a project.

## Dependencies and CI

- Renovate opens dependency PRs with a 3-day release-age gate and raises security PRs at once. Do not bypass the gate, and
  take majors deliberately.
- Security fixes here have gone out as version bumps in `pom.xml` (for example Netty, Tomcat and Vite). Name the advisory in
  the PR body.
- Workflow actions are pinned by version tag, so bump them through Renovate.

## Commits

- Commit subjects follow `type: summary` or `type(scope): summary` (`feat`, `fix`, `chore`, `docs`), as in the history. No
  `Co-Authored-By` trailer unless asked.
