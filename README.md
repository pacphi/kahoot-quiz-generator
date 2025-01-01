# Kahoot Quiz Generator

* [Background](#background)
* [Getting started](#getting-started)
* [Promot history](#prompt-history)
* [Build attempts](#build-attempts)
* [Prerequisites](#prerequisites)
* How to
  * [Clone](#how-to-clone)
  * [Build](#how-to-build)
  * [Run](#how-to-run)

## Background

My spouse wanted to create a number of themed New Year's Eve trivia matches hosted on [Kahoot](https://kahoot.com/).

While anyone can sign up for a free account and work thru the user interface to curate a quiz, I wanted to make it easier and less tedious to generate quizzes.

It's a great candidate use-case for applying an LLM, mixing in a minimal amount of business logic.

> Note: AI tools are available starting with a [Personal Kahoot+ Gold](https://kahoot.com/register/pricing-personal/) account, [here](https://support.kahoot.com/hc/en-us/articles/17152945038355-How-to-use-Kahoot-AI-tools) which includes the ability to generate questions based on a topic.

I thought, why not use this as an opportunity to flex [Claude](https://claude.ai). See how far I can go creating an agent with my favorite tech stack.  And stay compliant with Kahoot's [Acceptable Use Policy](https://trust.kahoot.com/acceptable-use-policy/).

Some trial and error resulted in me changing my approach;  consequently when I learned that Kahoot's Reporting API was limited to read-only operations.

What I ultimately settled on was the ability to produce quiz questions [packaged for upload](https://kahoot.com/blog/2018/08/23/import-kahoot-from-spreadsheet/) conforming to Kahoot's [Quiz spreadsheet template](https://kahoot.com/library/quiz-spreadsheet-template/).

## Getting started

I got started with:

* A Github [account](https://github.com/signup)
* A Claude.ai Pro [plan](https://www.anthropic.com/pricing)
* This Spring Initializr [configuration](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.4.1&packaging=jar&jvmVersion=21&groupId=me.pacphi&artifactId=kahoot-quiz-generator&name=Kahoot%20Quiz%20Generator&description=I%20create%20quizzes%20for%20you%20in%20Kahoot%20based%20on%20your%20prompt&packageName=me.pacphi.kahoot&dependencies=spring-ai-openai,web,configuration-processor,devtools,docker-compose)
* Kahoot credentials

## Prompt history

What follows here are the prompts I employed to help me code generate some pieces of this application.  If you're not interested, skip to [How to clone](#how-to-clone).

### Initial prompt

"I would like to build a chat interface that allows for user to generate questions with 4 multiple choice answers where only one answer is correct.  These  will then be automatically uploaded to Kahoot via API calls.  Can you implement this in ReactJS for the front-end and Spring Boot and Spring AI on the backend?"

### Second prompt

"Actually, what I require is something where a user could type in a chat request like:

I am curating a set of questions for New Year's Eve trivia match hosted on Kahoot.  I need you to construct a teen appropriate set of questions with four alternative answers for each of 20 questions.  Each question has only one correct answer.  Go.

And that request would invoke appropriate set of APIs.

Those APIs are available in this Open API spec"


> The Kahoot API specification was found [here](https://results.kahoot.com/openapi).  I actually followed a link from [here](https://results.kahoot.com/swagger/). But where I actually started searching was within some public documentation [here](https://support.kahoot.com/hc/en-us/articles/11735948502931-Guide-to-Kahoot-reports-API).

### Research prompts

"I see you created domain objects like KahootBlock and KahootResponse.  I assume you constructed these from the OpenAPI spec?  What Java tools exist to read a spec and code generate these model objects automatically?"


I chose [this](https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator-maven-plugin/README.md) Maven plugin.


"When I used the suggested plugin with mvn package I saw:

```bash
[WARNING] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/src/main/resources/openapi/kahoot-openapiv3-spec.yml [0:0]: unexpected error in Open-API generation
org.openapitools.codegen.SpecValidationException: There were issues with the specification. The option can be disabled via validateSpec (Maven/Gradle) or --skip-validate-spec (CLI).
 | Error count: 1, Warning count: 33
Errors:
	-attribute components.securitySchemes.ClientCredentials.scopes is missing
Warnings:
	-attribute components.securitySchemes.ClientCredentials.scopes is missing
```

How might I fix that?"


Claude mentioned that I had to add `scopes` to my spec, like so:

```yaml
components:
  securitySchemes:
    ClientCredentials:
      type: oauth2
      description: Use client id and secret for authorisation
      name: bearer
      scheme: bearer
      bearerFormat: Opaque
      flows:
        clientCredentials:
          tokenUrl: https://access-2.kahoot.com/auth/realms/kahoot-api/protocol/openid-connect/token
          scopes: {} # Add this empty scopes object to fix the error
```

"You generated a front-end and back-end for me.  I want to be able to build both from Maven. Is there a way I can execute mvn package then mvn spring-boot:run to have this just work?  How does the app consume the ReactJS you created?  Let me know the easiest way to do this, then show me the implementation and any changes/additions to project structure."

I wanted to provide a seamless development experience building and running a Spring Boot application that has a ReactJS front-end using Maven.  Claude didn't let me down.  It recommended that I leverage the [frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin).


"Where do I place the Quiz Generator Form Component within the project structure?  And can you explicitly tell me what other supporting files I should add to have the build complete successfully?"

Claude told me how and where to place .css, .html, .json and .jsx files.

So, the front-end consists of:

```bash
+- src
  +- main
    +- frontend
      - index.html
      - package.json
      - postcss.config.js
      - tailwind.config.js
      - vite.config.js
      +- src
        - App.jsx
        - index.css
        - main.jsx
        +- components
          - QuizForm.jsx
          +- ui
            - alert.jsx
            - card.jsx
```

### Refinement prompts

"Can you replace RestTemplate usage in KahootService with RestClient? And just constructor inject an instance of it?"

"Can you replace occurrence of OpenAiClient with ChatClient.  Maybe inject a ChatModel, like so

```java
public KahootService(ChatModel model, RestClient.Builder restClientBuilder) {
 this.chatClient = ChatClient.builder(model)
 .defaultAdvisors(
new SimpleLoggerAdvisor())
 .build();
```

We want to use chatClient methods so we have flexibility to swap chat model providers."

"Can you generate a Java record for KahootQuestion for me that's used in the KahootService?"


## Build attempts

### First try

I ran

```bash
mvn package
```

and got

```bash
[WARNING] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/src/main/resources/openapi/kahoot-openapiv3-spec.yml [0:0]: unexpected error in Open-API generation
org.openapitools.codegen.SpecValidationException: There were issues with the specification. The option can be disabled via validateSpec (Maven/Gradle) or --skip-validate-spec (CLI).
 | Error count: 1, Warning count: 33
Errors:
	-attribute components.securitySchemes.ClientCredentials.scopes is missing
Warnings:
	-attribute components.securitySchemes.ClientCredentials.scopes is missing
```

### Second try

I edited [openapi/kahoot-openapiv3-spec.yml](src/main/resources/openapi/kahoot-openapiv3-spec.yml) as suggested by addding `scopes: {}`.

Then tried

```bash
mvn package
```

and got compilation errors like

```bash
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/api/AnswerResourceApi.java:[34,24] package javax.annotation does not exist
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/api/AnswerResourceApi.java:[36,2] cannot find symbol
  symbol: class Generated
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/AnsweredQuestion.java:[11,41] package org.openapitools.jackson.nullable does not exist
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/AnsweredQuestion.java:[19,24] package javax.annotation does not exist
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/AnsweredQuestion.java:[26,2] cannot find symbol
  symbol: class Generated
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/ParticipantAnswer.java:[11,41] package org.openapitools.jackson.nullable does not exist
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/ParticipantAnswer.java:[13,41] package org.openapitools.jackson.nullable does not exist
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/ParticipantAnswer.java:[21,24] package javax.annotation does not exist
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/ParticipantAnswer.java:[28,2] cannot find symbol
  symbol: class Generated
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/AnswerStatus.java:[6,41] package org.openapitools.jackson.nullable does not exist
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/AnswerStatus.java:[14,24] package javax.annotation does not exist
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/AnswerStatus.java:[23,2] cannot find symbol
  symbol: class Generated
[ERROR] /Users/cphillipson/Documents/development/apps/kahoot-quiz-generator/target/generated-sources/openapi/src/gen/java/main/me/pacphi/kahoot/model/ParticipantAnswer.java:[35,11] cannot find symbol
  symbol:   class JsonNullable
  location: class me.pacphi.kahoot.model.ParticipantAnswer
```

and that meant I had to add [another configuration option](https://stackoverflow.com/a/75743432) to the OpenAPI Maven plugin.

### Third try

After editing [pom.xml](pom.xml), I tried

```bash
mvn package
```

I got compilation errors like

```bash
package jakarta.validation does not exist
package org.openapitools.jackson.nullable does not exist
```

which meant I had to add two more dependencies

```yaml
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

to my pom.xml.

### Fourth try

This time, compilation was successful! However, the build failed during the test phase with

```bash
Caused by: java.lang.IllegalArgumentException: OpenAI API key must be set. Use the connection property: spring.ai.openai.api-key or spring.ai.openai.chat.api-key property.
	at org.springframework.util.Assert.hasText(Assert.java:253)
	at org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.resolveConnectionProperties(OpenAiAutoConfiguration.java:103)
	at org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.openAiApi(OpenAiAutoConfiguration.java:160)
	at org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.openAiChatModel(OpenAiAutoConfiguration.java:121)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.lambda$instantiate$0(SimpleInstantiationStrategy.java:171)
```

And that made sense, because I hadn't yet added an API key for the [dependency on Open API](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html).

### Fifth try

Before I attempted another build, I decided to convert `application.properties` to `application.yml`, then grab and integrate configuration from [another project](https://github.com/cf-toolsuite/sanford).  I also added a few select dependencies to my `pom.xml` to prepare for deploying this application to different container runtimes.

And this time, after executing `mvn package`, I was rewarded with

```bash
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  8.685 s
[INFO] Finished at: 2024-12-28T05:22:02-08:00
[INFO] ------------------------------------------------------------------------
```

#### Integrating the source code Claude drafted for me

At this point I thought I was ready to copy-paste the code Claude had written for me.  But, that turned out to be a dead-end road.

##### Backend struggles

I have to admit that it took some iterative refinement on my part, and some familiarity with Spring AI (at the time of this writing, taking advantage of the structured output converter capabilities for [generic bean types](https://docs.spring.io/spring-ai/reference/api/structured-output-converter.html#_generic_bean_types) in the 1.0.0-M5 release) to get the [KahootService](src/main/java/me/pacphi/kahoot/service/KahootService.java) implementation beaten into shape.  Claude takes you so far, but you're going to need to roll up your own sleeves.

> This is because foundation models have been trained on data available within a particular time-frame.  Spring AI in particular is a rather new addition within the Spring eco-system; Claude was only aware of API up to the 0.8 release.

### Sixth try

Because I hit a dead-end - Kahoot's Reporting API limited to read-only operations - I had to rethink the function of this agent.

I started a new chat with Claude and entered this prompt

"You are a Java and Spring expert. You are also aware of Apache POI for generating Microsoft Office documents like Excel (.xlsx) spreadsheets.  I want to build an application that has both a front-end and back-end. The front-end I'd like you to implement with ReactJS, the back end wth Java and Spring an other necessary dependencies.  The project will be built with Maven.  The application will take as input from the user: a) a topic, theme or directive and b) a number of questions in order to generate  a Kahoot quiz.  The quiz questions and multiple choice answers will be written to an Excel (.xlsx) file adhering to the format and validation of the Kahoot Quiz template. I need you to consider the use of the Kahoot Reporting API spec, but I will provide model object named KahootQuestion.  We'll use Spring AI's ChatModel support to generate the questions.  I'm going to feed you a few pieces, then I'd like you to draft a complete implementation."

```java
import java.util.List;
/**
 * Represents a single Kahoot quiz question with multiple choice answers.
 *
 * @param question The text of the question
 * @param choices List of possible answers, where exactly one must be correct
 */
public record KahootQuestion(
String question,
List<Choice> choices) {
 /**
 * Represents a single answer choice for a Kahoot question.
 *
 * @param answerText The text of the answer choice
 * @param isCorrect Whether this is the correct answer
 */
public record Choice(
String answerText,
boolean isCorrect) {
 /**
 * Validates that the choice has non-empty answer text.
 */
public Choice
 {
if (answerText == null || answerText.trim().isEmpty()) {
throw new IllegalArgumentException("Answer text cannot be empty");
 }
 }
 }
 /**
 * Validates that the question is properly formed:
 * - Question text is not empty
 * - Has exactly 4 choices
 * - Exactly one choice is marked as correct
 */
public KahootQuestion
 {
if (question == null || question.trim().isEmpty()) {
throw new IllegalArgumentException("Question text cannot be empty");
 }
if (choices == null || choices.size() != 4) {
throw new IllegalArgumentException("Must have exactly 4 choices");
 }
long correctCount = choices.stream()
 .filter(Choice::isCorrect)
 .count();
if (correctCount != 1) {
throw new IllegalArgumentException("Must have exactly one correct answer");
 }
 }
}

public KahootResponse generateQuestions(String prompt) {
 SystemMessage systemMessage = new SystemMessage("""
 You are a Kahoot quiz generator. Generate engaging and age-appropriate questions.
 Each response should be a valid JSON array of 20 question objects.
 Each question object should have:
 - question (string)
 - choices (array of 4 objects with answerText and isCorrect)
 - Only one choice should have isCorrect=true
 """);
UserMessage userMessage = new UserMessage("Generate quiz questions for: " + prompt);
var chatPrompt = new Prompt(List.of(systemMessage, userMessage));
var chatResponse = chatClient.prompt(chatPrompt).call();
List<KahootQuestion> questions = chatResponse.entity(new ParameterizedTypeReference<List<KahootQuestion>>() {});
```

> I also added a copy the template to the prompt above.  As well, I added a copy of the Reporting API spec.  The latter wasn't necessary in hindsight.

And Claude gave me enough to take this the rest of the way home.

## Prerequisites

* Git CLI
* An Open AI or Groq Cloud account
* Java SDK 21
* Maven 3.9.9

## How to clone

with Git CLI

```bash
git clone https://github.com/pacphi/kahoot-quiz-generator
```

with Github CLI

```bash
gh repo clone pacphi/kahoot-quiz-generator
```

## How to build

Open a terminal shell, then execute:

```bash
cd kahoot-quiz-generator
mvn clean package
```

## How to run

After building and before attempting to run you must:

* create a `config` folder which would be a sibling of the `src` folder.  Create a file named `creds.yml` inside that folder.  Add your own API key into that file.

```yaml
spring:
  ai:
    openai:
      api-key: {REDACTED}
```
> Replace `{REDACTED}` above with your Groq Cloud or OpenAI API key.

Then execute:

```bash
mvn spring-boot:run
```

Open your favorite browser and visit `http://localhost:8080`.

> Back in terminal shell, press Ctrl+C to shutdown.
