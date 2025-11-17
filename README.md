# Insurance Agentic Demo

This Spring Boot service showcases how to orchestrate multiple LangChain4j agents backed by local Ollama models to handle vehicle insurance workflows end to end—quote generation, claim intake, automated approval, and underwriter escalation.

> This demo project was created using Cursor, using Java&nbsp;25, Spring Boot&nbsp;4.0.0-SNAPSHOT, LangChain4j by giving the following prompt:
>
> ```
> You are a Senior Java Developer your task is to create a AI agent for insurance that will provide insurance quote for vehicle based on information user provids, log claims and approvel them to certail amount or notify the underwritter if there are any suspecions or in case of high claims amount. You can use existing project. Java 25 is present in system so use Java 25 features where possible, use the provided Spring Boot version, Use langchain4j "https://docs.langchain4j.dev/tutorials/agents#agentic-systems" framework to develop agent. Ollama server is runnning locally and following models are already pulled "llama3.1:latest" and "deepseek-r1:latest". You can use either one or both of them. Once you complete the code test the code and see if it is running as expected. You can also write Junut test cases.
> ```

## Architecture

- **LangChain4j Agentic Workflow** – Quote, claim logging, approval, and underwriter notification agents are typed interfaces annotated with `@Agent`. They are assembled through `AgenticServices`, following the agentic workflow guidance in the LangChain4j docs ([source](https://docs.langchain4j.dev/tutorials/agents#agentic-systems)).
- **Supervisor-ready design** – Each agent can be reused in future supervisor/loop workflows if we extend into more advanced orchestrations.
- **Ollama-backed models** – `llama3.1:latest` handles general reasoning, while `deepseek-r1:latest` is used for claim-approval planning.
- **Spring Boot 4** – Provides REST endpoints under `/api/insurance` for quoting, claims, and health checks.
- **DTOs & Parsing Helpers** – Responses from LLM agents are parsed into rich domain objects (`InsuranceQuote`, `Claim`, `UnderwriterNotification`).

## Requirements

- Java 25 (Temurin or Oracle JDK)
- Gradle wrapper (included)
- Running Ollama server with `llama3.1:latest` and `deepseek-r1:latest` models pulled

## Getting Started

1. Ensure Ollama is running locally (`ollama serve`) and both models are available.
2. Configure any additional settings via `src/main/resources/application.properties` if needed (default endpoints already set to `http://localhost:11434`).
3. Launch the API:
   ```bash
   ./gradlew bootRun
   ```
4. Hit the endpoints:
   - `POST /api/insurance/quote`
   - `POST /api/insurance/claim`
   - `GET /api/insurance/health`

## Running Tests

Execute the full test suite (unit-level, no real LLM calls required):

```bash
./gradlew test
```

## Key Files

| Path | Description |
| --- | --- |
| `src/main/java/com/cursor/ai/agentdemo/agent/*.java` | LangChain4j agent interfaces for quoting, claims, approval, and underwriter notices |
| `src/main/java/com/cursor/ai/agentdemo/service/InsuranceAgentService.java` | Orchestrates the agent workflow and response parsing |
| `src/main/java/com/cursor/ai/agentdemo/controller/InsuranceController.java` | REST API layer |
| `src/test/java/com/cursor/ai/agentdemo/*` | Unit tests with stubbed agents/services |

## Future Enhancements

- Persist quotes/claims to a database.
- Add LangChain4j supervisor agents to decide dynamically which sub-agent to invoke based on conversation context.
- Introduce tool-calling for verifying VINs or fetching repair estimates before approval decisions.

