# CLAUDE.md — WorkFlex Coding Challenge

## Context
Timed coding challenge (2.5h): import workations from a CSV into a database and show them in a sortable table.
Evaluated on: completeness of functionality, architecture, code quality & style, testing.
Stack is fixed: Java + Spring Boot (backend), Angular + TypeScript (frontend).
Priority order: working end-to-end features > tests > polish > extras. Never leave the app in a broken state.

## Environment
- OS: Windows 11. IDE: IntelliJ (Claude Code integrated), VS Code.
- Java 21 (Temurin), Spring Boot 4.1.x, Maven (wrapper), configuration in `application.yaml`.
- Node 24.19, Angular CLI 22, npm 11.
- Base package: `com.hernanaguado.backend`.
- Commands (use the wrapper, never a global mvn):
  - Backend run: `./mvnw spring-boot:run` (PowerShell/cmd: `.\mvnw spring-boot:run`) → http://localhost:8080
  - Backend tests: `./mvnw test`
  - Frontend run: `ng serve` → http://localhost:4200 · build: `ng build` · tests: `ng test --watch=false`

## Repository layout
```
/backend    Spring Boot app
/frontend   Angular app
/docs       screenshot.png of the table
README.md   How to run, design decisions, trade-offs, what I'd improve
CLAUDE.md   This file
```
`challenge/` (the original PDF, CSV and icons) is git-ignored and must not be committed.

## Backend conventions
- Package by feature, layered inside: `controller` → `service` → `repository`, plus `dto`, `model` (entities), `mapper`, `importer`, `exception`.
- Concerns not specific to one feature live at the top level: `config` (CORS), `exception` (global handler), `country` (`CountryCodeResolver`).
- Controllers are thin: no business logic, only HTTP mapping.
- Never expose JPA entities in the API. DTOs are Java `record`s with explicit mappers.
- Constructor injection only (no field `@Autowired`); `final` fields. Lombok allowed on entities (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`); avoid `@Data` on entities.
- Centralized error handling with `@RestControllerAdvice` (extends `ResponseEntityExceptionHandler`) returning `ProblemDetail` (RFC 7807); unexpected errors → 500 without leaking internals.
- REST endpoint path is fixed by the spec: `GET /workflex/workation` (list of all workations, no pagination).
- Persistence: Spring Data JPA + H2 in-memory (zero setup for the reviewer). H2 console at `/h2-console`.
- Data loading: the CSV is imported by the Java app, never via `data.sql`. `WorkationCsvImporter` (`ApplicationRunner`) reads `workflex.import.csv-location` (default `classpath:workations.csv`) and parses it with Apache Commons CSV in `WorkationCsvParser`. Import only when the table is empty (idempotent). Invalid rows are skipped with a WARN log including the physical CSV line number; valid rows are still imported.
- Risk levels: `RiskLevel { HIGH, LOW, NO }`; `fromCsv` also accepts `LOW_RISK` / `NO_RISK`, case-insensitive.
- Country codes (lowercase ISO alpha-2, `null` if unknown) are resolved at mapping time from English names via `Locale`, not stored.
- CORS: allowed origins from `workflex.cors.allowed-origins` (`http://localhost:4200`) in a single `WebMvcConfigurer` (`WebConfig`), not `@CrossOrigin`.
- All custom config uses the `workflex.*` prefix. No hardcoded URLs or secrets.
- OpenAPI via SpringDoc; Swagger UI at `/swagger-ui.html`. `@Operation`/`@Tag` only where they add clarity.
- Small methods, meaningful names, no dead or commented-out code.

## Spring Boot 4 gotchas (do not use Boot 3 APIs)
- `@MockBean` is removed → use `@MockitoBean` (`org.springframework.test.context.bean.override.mockito.MockitoBean`).
- `@WebMvcTest` / `@AutoConfigureMockMvc` live in `org.springframework.boot.webmvc.test.autoconfigure` and need `spring-boot-starter-webmvc-test` (test scope).
- Jackson 3: `ObjectMapper` is `tools.jackson.databind.ObjectMapper` (not `com.fasterxml.jackson.databind`). Jackson annotations keep the `com.fasterxml.jackson.annotation` package. `LocalDate` serializes as ISO by default.
- Starters are modular; if a class doesn't resolve, the fix is usually a missing module starter, not a version change.

## Backend testing
- JUnit 5 + Mockito + AssertJ. Behavior-describing names: `shouldSkipImportWhenWorkationsAlreadyExist()`.
- Unit tests: `RiskLevel`, `CountryCodeResolver`, CSV parser, importer (incl. idempotency and `src/test/resources/workations-edge-cases.csv`), mapper, service.
- Controller slice test with `@WebMvcTest` + `MockMvc`: JSON shape, ISO dates, ProblemDetail on 500, CORS header.
- One `@SpringBootTest` verifying the 5 CSV rows are returned by `/workflex/workation` and re-running the importer doesn't duplicate them.
- Use `OutputCaptureExtension` to assert WARN logs.

## Frontend conventions (Angular 22)
- Standalone components only. `provideHttpClient(withFetch())` and `provideRouter()` in `app.config.ts`.
- Signals for component state; built-in control flow (`@if`, `@for`, `@switch`).
- Feature folders: `features/<feature>/` with components, a `*.service.ts` for HTTP, and `*.model.ts` interfaces matching backend DTOs.
- Components never call `HttpClient` directly; they use services.
- API base URL only in `src/environments/environment.ts` (`http://localhost:8080/workflex`); no dev proxy — the backend's CORS config is used.
- Table built with Angular Material: `mat-table` + `MatTableDataSource` + `matSort`, with a custom `sortingDataAccessor` (risk by severity HIGH > LOW > NO; ISO dates sort chronologically). Dates shown `dd/MM/yyyy` via `DatePipe`.
- Risk display: HIGH → red icon "High risk"; LOW → yellow icon "No risk"; NO → green icon "No risk". Icons in `public/icons/`, logo in `public/`.
- Flags from `https://flagcdn.com/w40/{code}.png`; hide the image when the code is `null`, keep the country name.
- Handle loading, empty and error states in the UI.
- Strict TypeScript, no `any`.
- Clean, simple, responsive layout with SCSS and the Material theme in `styles.scss`.

## Frontend testing
- Test runner: Vitest (generated by the CLI); don't switch runners.
- Service tests with `provideHttpClient()` + `provideHttpClientTesting()` + `HttpTestingController`.
- Component tests with the service mocked (`of` / `throwError`): rows rendered, date format, sorting via header clicks, risk labels/icons, flags, empty and error states. Shared test data in `workation.fixtures.ts`.

## Workflow rules for the AI assistant
- Work in small slices; each slice is verified by build, a manual run and its tests before moving on.
- Run the backend and frontend test commands (and `ng build`) before declaring any step done.
- Never `git commit` or `git push` without the user's explicit OK; propose Conventional Commit messages (`feat:`, `test:`, `fix:`, `docs:`, `chore:`) and wait.
- Don't add libraries unless they clearly save time; justify any addition.
- Don't over-engineer (no hexagonal/CQRS for a 2.5h challenge); clean layering is enough.
- Ask before deleting or restructuring existing code.
- Keep answers short; show diffs or files, not long explanations.

## README must include
- Prerequisites and exact commands to run backend, frontend and tests.
- Endpoint overview and link to Swagger UI.
- Design decisions and trade-offs.
- Assumptions made about ambiguous requirements.
- Use of AI tooling.
- What I would do with more time.