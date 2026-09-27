# Incident Simulator

Incident Simulator is a Spring Boot service that generates realistic production-incident event timelines using a Gemini-powered AI assistant, persists those events, and exposes them through REST and SSE endpoints.

## Tech Stack

- Java 21
- Spring Boot 3.3.4 (Web, Validation, Data JPA)
- LangChain4j + Google Gemini
- MySQL (configured in `application.properties`)

## Request Flow (Happy Path)

### 1) Incident simulation (`GET /simulate/{message}`)

1. Client sends a simulation prompt to `IncidentController.simulateIncidents`.
2. `IncidentController` delegates to `IncidentService.simulateIncident(message)`.
3. `IncidentService` calls `IncidentAssistant.chat(message)`.
4. `IncidentAssistant` (configured in `AIConfig`) uses Gemini + tool access (`Tools`) and returns an `IncidentPlan`.
5. `IncidentService.processPlan(plan)`:
   - Computes the next `incidentId` via `EventsRepository.findMaxIncidentId()`.
   - Converts each `EventDTO` to `Events` entity.
   - Saves each row in `events` table using `EventsRepository.save(...)`.
6. The same generated `IncidentPlan` is returned to the caller.

### 2) Paginated events fetch (`GET /incidents/records?page=n` and `GET /events?page=n`)

1. Client requests a page.
2. Controller (`IncidentController` or `ChatController`) creates `PageRequest.of(page, 10)`.
3. `EventsRepository.findAll(pageable)` loads data from DB.
4. A paginated `Page<Events>` response is returned.

### 3) Event stream playback (`GET /events/stream`)

1. Client opens SSE connection to `EventsStreamController.streamEvents()`.
2. Controller calls `IncidentService.streamEvents()`.
3. Service loads all events sorted by `id` ascending.
4. Service emits each event through `SseEmitter` with random delay (1–3 seconds).
5. Stream completes after final event.

## Core Services and Responsibilities

### Controllers

- **`IncidentController`**
  - Starts incident simulation (`/simulate/{message}`)
  - Returns paginated incident records (`/incidents/records`)

- **`ChatController`**
  - Free-form assistant chat endpoint (`/chat/{msg}`)
  - Returns paginated events (`/events`)

- **`EventsStreamController`**
  - SSE endpoint for replaying stored events (`/events/stream`)

### Application Services

- **`IncidentService`**
  - Orchestrates incident generation flow
  - Converts `EventDTO` → `Events`
  - Assigns and increments logical `incidentId`
  - Persists generated events
  - Streams stored events over SSE

- **`ChatService`**
  - Wraps generic assistant conversation (`Assistant.chat`)

### AI Layer

- **`AIConfig`**
  - Builds `GoogleAiGeminiChatModel`
  - Wires `IncidentAssistant` and `Assistant` beans with chat memory and tool access

- **`IncidentAssistant`**
  - Structured AI contract that must return an `IncidentPlan`

- **`Assistant`**
  - Generic chat contract for natural-language responses

- **`Tools`**
  - Exposes enum catalogs (services/components/severities)
  - Exposes DB-backed helper queries (event count, filter by severity/component/service)

### Persistence

- **`EventsRepository`**
  - Standard JPA CRUD/pagination for `Events`
  - Finder methods by severity/component/service
  - Native query for max `incident_id`

### Configuration

- **`GlobalConfig`**
  - Enables permissive CORS mappings for all routes

## Entity and DTO Structure

### `Events` entity (`events` table)

Represents one persisted incident log event.

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | Primary key, auto-generated |
| `incidentId` | `Long` | Logical group identifier for one generated incident chain |
| `timestamp` | `LocalDateTime` | Event time in timeline |
| `severity` | `Severity` | Enum stored as string |
| `service` | `Service` | Enum stored as string |
| `component` | `Component` | Enum stored as string |
| `message` | `String` | Event message (`TEXT`) |
| `stackTrace` | `String` | Optional stack trace (`TEXT`) |

### `EventDTO`

AI-generated event payload used before persistence. Mirrors core event fields:

- `timestamp`
- `severity`
- `service`
- `component`
- `message`
- `stackTrace`

`IncidentService.EventDTOToEvents(...)` maps this DTO to the `Events` entity.

### `IncidentPlan`

Top-level structured response from `IncidentAssistant`:

- `events: List<EventDTO>` → full incident timeline
- `responseMessage: String` → optional assistant message

### Enums

- **`Severity`**: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`
- **`Service`**: `PAYMENT_SERVICE`, `ORDER_SERVICE`, `USER_SERVICE`, `INVENTORY_SERVICE`, `AUTH_SERVICE`, `NOTIFICATION_SERVICE`, `SEARCH_SERVICE`, `API_GATEWAY`, `REPORTING_SERVICE`, `ANALYTICS_SERVICE`
- **`Component`**: `DATABASE`, `CACHE`, `MESSAGE_QUEUE`, `API`, `CONNECTION_POOL`, `HTTP_CLIENT`, `FILE_SYSTEM`, `CPU`, `MEMORY`, `NETWORK`

## API Summary

- `GET /simulate/{message}` → generate and persist one incident plan
- `GET /incidents/records?page=0` → paginated incident records (10/page)
- `GET /events?page=0` → paginated events (10/page)
- `GET /events/stream` → server-sent event stream of all stored events
- `GET /chat/{msg}` → generic assistant chat response
