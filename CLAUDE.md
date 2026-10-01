# CLAUDE.md — WorkFlex Coding Challenge

## Context
Timed coding challenge (2.5h). Evaluated on: completeness of functionality, architecture, code quality & style, testing.
Stack is fixed: Java + Spring Boot (backend), Angular + TypeScript (frontend).
Priority order: working end-to-end features > tests > polish > extras. Never leave the app in a broken state; commit after every working step.

## Environment
- OS: Windows 11. IDE: IntelliJ (Claude Code integrated), VS Code.
- Java 21 (Temurin), Spring Boot 4.1.x, Maven (wrapper), configuration in YAML.
- Node 24 LTS, Angular CLI latest, npm.
- Base package: `com.hernanaguado.backend`.
- Commands (use the wrapper, never a global mvn):
  - Backend run: `./mvnw spring-boot:run` (PowerShell/cmd: `.\mvnw spring-boot:run`)
  - Backend tests: `./mvnw test`
  - Frontend run: `ng serve` · build: `ng build` · tests: `ng test --watch=false`

## Repository layout
```
/backend    Spring Boot app
/frontend   Angular app
README.md   How to run, design decisions, trade-offs, what I'd improve
CLAUDE.md   This file
```

## Backend conventions
- Package by feature, layered inside: `controller` → `service` → `repository`, plus `dto`, `model` (entities), `mapper`, `exception`. Shared config in `config`.
- Controllers are thin: no business logic, only HTTP mapping, `@Valid` and status codes.
- Never expose JPA entities in the API. DTOs are Java `record`s with explicit mappers.
- Constructor injection only (no field `@Autowired`); `final` fields. Lombok allowed on entities (`@Getter`, `@Setter`, `@NoArgsConstructor`); avoid `@Data` on entities.
- Validation with Jakarta Bean Validation on request DTOs.
- Centralized error handling with `@RestControllerAdvice` returning `ProblemDetail` (RFC 7807). Custom exceptions (`ResourceNotFoundException`, etc.) → 404/400/409.
- REST: `/api/<plural-resource>`, correct verbs and status codes (201 + Location on create, 204 on delete), pagination if lists can grow.
- Persistence: Spring Data JPA + H2 in-memory (zero setup for the reviewer). Seed data via `src/main/resources/data.sql` with `spring.jpa.defer-datasource-initialization: true`. If seed data comes as JSON, load it with a `CommandLineRunner`.
- CORS: allow `http://localhost:4200` in a single `WebMvcConfigurer` config class, not with `@CrossOrigin` on controllers.
- OpenAPI via SpringDoc; Swagger UI at `/swagger-ui.html`. Add `@Operation`/`@Tag` only where it adds clarity.
- Config in `application.yml`. No hardcoded URLs or secrets.
- Small methods, meaningful names, no dead or commented-out code.

## Spring Boot 4 gotchas (do not use Boot 3 APIs)
- `@MockBean` is removed → use `@MockitoBean` (`org.springframework.test.context.bean.override.mockito.MockitoBean`).
- `@WebMvcTest` lives in `org.springframework.boot.webmvc.test.autoconfigure` and needs `spring-boot-starter-webmvc-test` (test scope). `@DataJpaTest` likewise needs its own test starter. Check `pom.xml` before adding imports.
- Jackson 3: `ObjectMapper` is `tools.jackson.databind.ObjectMapper` (not `com.fasterxml.jackson.databind`). Jackson annotations keep the `com.fasterxml.jackson.annotation` package.
- Starters are modular; if a class doesn't resolve, the fix is usually a missing module starter, not a version change.

## Backend testing
- Unit tests for every service: JUnit 5 + Mockito + AssertJ. Cover happy path and error cases (not found, validation, edge cases).
- Controller slice tests with `@WebMvcTest` + `MockMvc`: status codes, JSON shape, validation errors, ProblemDetail on 404.
- One `@SpringBootTest` that the context loads and seed data is present.
- Behavior-describing names: `shouldReturn404WhenItemDoesNotExist()`.

## Frontend conventions (Angular, latest stable)
- Standalone components only. `provideHttpClient()` and `provideRouter()` in `app.config.ts`.
- Signals for component state; built-in control flow (`@if`, `@for` with `track`).
- Feature folders: `features/<feature>/` with components, a `*.service.ts` for HTTP, and `*.model.ts` interfaces matching backend DTOs.
- Components never call `HttpClient` directly; they use services.
- API base URL in `environments` (or `proxy.conf.json`), never hardcoded in services.
- Handle loading, empty and error states in the UI.
- Strict TypeScript, no `any`.
- Clean, simple, responsive layout with SCSS. The provided logo goes in `public/`.
- Reactive forms with validation if there are forms.

## Frontend testing
- Service tests with `provideHttpClient()` + `provideHttpClientTesting()` + `HttpTestingController`.
- Component tests for main components: renders data, loading/error states, user interactions.
- Use whatever test runner the CLI generated; don't switch runners.

## Workflow rules for the AI assistant
- First step: read the challenge PDF and restate the requirements as a checklist, marking must-have vs nice-to-have. Wait for confirmation before coding.
- Implement in small vertical slices (backend endpoint + test → frontend consumption + test). After each slice: build passes, tests pass, then propose a Conventional Commit message (`feat:`, `test:`, `fix:`, `docs:`, `chore:`).
- Run the backend and frontend test commands before declaring any step done.
- Don't add libraries unless they clearly save time; justify any addition.
- Don't over-engineer (no hexagonal/CQRS for a 2.5h challenge); clean layering is enough.
- Ask before deleting or restructuring existing code.
- Keep answers short; show diffs or files, not long explanations.

## README must include
- Prerequisites and exact commands to run backend, frontend and tests.
- Endpoints overview (and link to Swagger UI).
- Design decisions and trade-offs.
- Assumptions made about ambiguous requirements.
- What I would do with more time.
