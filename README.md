# AI Agent — Autonomous Multi-Agent Framework

A lightweight framework for building **autonomous multi-agent systems** on top of [Spring AI](https://spring.io/projects/spring-ai) and the **A2A (Agent-to-Agent)** protocol. Each agent runs as an independent Spring Boot application and can act as an orchestrator, a worker, or both. Agents discover each other, delegate tasks, and combine results — all driven by configuration, no custom code required.

---

## How it works

- Every agent is configured via two YAML files: `agent.yaml` (identity, role, prompt, skills) and `mcp.yaml` (external tool connections via MCP).
- **Orchestrator** agents receive a trigger task on startup, break it down, and delegate sub-tasks to registered sub-agents using A2A tool calls.
- **Worker** agents expose their skills over A2A and respond to requests from orchestrators.
- Agents can connect to external tools and APIs through [MCP (Model Context Protocol)](https://modelcontextprotocol.io) clients, with support for OAuth2 and other auth types.

```
┌──────────────┐        A2A        ┌──────────────────┐
│  Team Lead   │ ───────────────►  │   Scrum Master   │
│ (orchestrator)│                  │    (worker)       │
└──────────────┘                   └──────────────────┘
       │                                    │
       └──── delegates tasks ────────────── └──── uses MCP tools
```

---

## Quick start

### Requirements

- Docker & Docker Compose

### Run the example

```bash
docker-compose up --build
```

This builds both agents from source and starts the full multi-agent system. The Team Lead waits for the Scrum Master to be healthy before sending its first task.

### Build only (no Docker)

```bash
./mvnw package -DskipTests
java -jar target/ai-agent-0.0.1-SNAPSHOT.jar
```

---

## Docker image

> **Coming soon** — a pre-built image will be published to Docker Hub. Once available, you can replace the `build: .` directive in `docker-compose.yml` with the image reference:

```yaml
# Placeholder — replace with the published image once available
image: nicholasmeyers/ai-agent:latest
```

---

## Configuration

Each agent is configured by mounting two files into the container at `/config/`:

| File | Purpose |
|---|---|
| `/config/agent.yaml` | Agent identity, system prompt, skills, sub-agent URLs |
| `/config/mcp.yaml` | MCP client connections (external tools & APIs) |

### `agent.yaml` reference

```yaml
agent:
  name: My Agent
  description: What this agent does
  url: http://my-agent:8080/a2a/       # how other agents reach this one
  version: 1.0.0
  prompt: |
    You are ...                        # system prompt sent to the LLM
  orchestrator: true                   # true = sends the trigger task on startup
  task: Do something useful            # only used when orchestrator: true
  sub-agents:
    - http://other-agent:8080/a2a      # agents this one can delegate to
  skills:
    - id: 00000000-0000-0000-0000-000000000001
      name: my skill
      description: What this skill does
      tags:
        - example
```

### `mcp.yaml` reference

```yaml
mcp:
  clients:
    - name: my-tool
      url: https://mcp.example.com
      auth-type: OAUTH2              # or NONE
      oauth:
        token-url: https://auth.example.com/token
        client-id: your-client-id
        client-secret: your-client-secret
```

### Environment variables

| Variable | Description |
|---|---|
| `OPENAI_BASE_URL` | Base URL of the OpenAI-compatible API endpoint |
| `OPENAI_API_KEY` | API key |
| `OPENAI_MODEL` | Model name (e.g. `gpt-4o`, `llama3`, `claude-3-5-sonnet`) |

---

## Example: Team Lead + Scrum Master

The `docker/` folder contains a ready-to-run example with two agents.

### Team Lead (orchestrator)

Configured in `docker/team-lead/config/agent.yaml`. On startup it sends the task `"Check if your team is working"` to itself, then delegates sub-tasks to the Scrum Master using A2A tool calls. After all sub-agents have responded it combines the results into a final answer.

**Skills exposed:**
- `orchestrate tasks` — breaks down a request and delegates to sub-agents

### Scrum Master (worker)

Configured in `docker/scrum-master/config/agent.yaml`. Listens for tasks from the Team Lead and responds using its own skills. It does not spawn sub-agents of its own.

**Skills exposed:**
- `create user stories` — translates requirements into user stories with acceptance criteria
- `plan sprint` — plans a sprint based on the backlog and team capacity

The Scrum Master also shows how to wire up an external MCP tool (`docker/scrum-master/config/mcp.yaml`) using OAuth2 — useful for connecting to project management systems.

---

## Tech stack

| Layer | Technology |
|---|---|
| Runtime | Java 25, Spring Boot 4.x |
| AI | Spring AI 2.x (`spring-ai-starter-model-openai`) |
| Agent protocol | A2A (`spring-ai-a2a-server-autoconfigure`) |
| Tool protocol | MCP (`spring-ai-starter-mcp-client`) |
| Container | Docker / Docker Compose |
