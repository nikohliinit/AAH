# Codebase Learning Document

**Project**: tms-shipment-load (service)
**Profiled**: 2026-11-16
**Primary Language**: Java 21
**Codebase Size**: 1,859 files, ~445K lines

---

## 1. System Purpose and Context

This is **TMS Shipment Load Integration Service** (`tms-shipment-load`) — Macy's Transportation Management System (TMS) middleware integration layer. Its job is to replace TIBCO middleware: consuming logistics events from Kafka and Google Pub/Sub, applying business rules and data enrichment, and translating them into REST calls to the Transportation Manager (TM) API.

Events it handles:
- **Shipment Create/Delete** — from VSP Kafka or Confluent Kafka
- **Ship Confirm** — from GCP Pub/Sub (appointment updates)
- **LTL Load Events** — Less-than-truckload tracking from Kafka
- **Trailer Dock/Door CDC** — Change-data-capture events for dock/door status
- **Trailer Image Attach** — Photo attachment to trailers in TM
- **Photo Grabber** — Scheduled GCS photo sync to TM
- **Orders Hub Check** — Order status validation

The service is part of a larger parent project `tms-shipment-load` (multi-module Maven) and runs as a standalone Spring Boot application.

## 2. Technology Stack

| Category | Technology | Version (if known) | Notes |
|----------|------------|-------------------|-------|
| Language | Java | 21 | Records, sealed classes available but not widely used |
| Framework | Spring Boot | (parent BOM) | Web, Actuator, JPA, Validation, Retry |
| Integration | Apache Camel | 4.1.0 | Routes + Processors; replaces TIBCO ESB |
| Messaging | Apache Kafka | camel-kafka 4.1.0 | Managed + Confluent variants |
| Messaging | GCP Pub/Sub | camel-google-pubsub 4.1.0 | Audit feedback + ship-confirm intake |
| Object Storage | GCP Storage | 4.0.0 | Photo assets |
| Secrets | CyberArk SDK | javapasswordsdk | Runtime credential retrieval for TM API |
| Secrets | GCP Secret Manager | 2.23.0 | GCP-managed secrets |
| Data | Spring Data JPA | BOM | Dock-door enrichment persistence |
| Tracing | Micrometer + OTEL | 1.3.5 | Distributed tracing |
| Logging | Log4j2 + LMAX Disruptor | BOM | Async structured logging |
| Build | Maven | 3.6.1 | Multi-module parent |

## 3. Architecture Overview

**Architectural style**: Event-driven integration microservice (replaces TIBCO ESB). The service is NOT a traditional request/response web service — it is a Camel-based message processor.

**Layer structure**:
1. **RouteBuilder layer** — Defines Camel DSL routes: consumes from Kafka/Pub/Sub, defines exception handling (redelivery policies per exception type), conditionally routes to processors. Routes are feature-flagged via `@ConditionalOnProperty`.
2. **Processor layer** — Stateless `Camel.Processor` implementations. Each handles one event type. They deserialize the message, validate it, call services, and publish audit feedback via `CommonUtil`.
3. **Service layer** — Thin business logic + data enrichment. Each service calls `TMRestApiClient` for outbound TM API calls.
4. **Client layer** — `TMRestApiClient` wraps Spring `RestTemplate`, retrieves credentials from CyberArk at runtime, and sends JAXB-serialized XML to TM REST API.

**Key design patterns**:
- **Camel Route + Processor** (not Controller-Service for messaging paths)
- **Feature flags** via `@ConditionalOnProperty` — routes can be toggled without code changes
- **Exception hierarchy** — each route has typed `onException` handlers with per-exception redelivery policies
- **Audit feedback loop** — every route publishes success/failure events to GCP Pub/Sub via `CommonUtil`
- **JAXB-generated DTOs** — TM API types come from WSDL, not hand-written
- **Dual Kafka support** — the same inbound route supports Managed Kafka and Confluent Cloud via `kafka.mode` property

## 4. Codebase Structure

| Directory | Purpose | Key Files |
|-----------|---------|-----------|
| `camel/routes/` | Camel DSL route definitions | `InboundShipmentRouteBuilder.java`, `UpdateTMRouteBuilder.java`, etc. |
| `camel/processors/` | Business event handlers | `CreateShipmentProcessor.java`, `UpdateTMProcessor.java` (most complex) |
| `service/` | Business logic + TM API orchestration | `InBoundShipmentServiceImpl.java`, `LoadEnrichmentServiceImpl.java` |
| `client/` | HTTP client for TM REST API | `TMRestApiClient.java` — handles auth + XML marshalling |
| `dto/inbound/` | Kafka message POJOs | `ShipmentEnterpriseMessage.java`, event type enums |
| `dto/gen/transportationmanager/` | JAXB-generated TM API types (~700 classes) | Do NOT hand-edit — regenerate from WSDL |
| `dto/enrichment/`, `dto/dockdoor/`, etc. | Domain-specific DTOs | Various enrichment/domain DTOs |
| `mapper/` | JAXB ↔ domain mapping | `LoadMapper.java` — central mapping hub |
| `util/` | Shared utilities | `CommonUtil.java` (audit feedback), `Constants.java` |
| `exception/` | Typed exception hierarchy | `TMSMappingException`, `TMSDataValidationException`, etc. |
| `config/` | Spring config beans | `LogInterceptor.java` (REST template logging) |
| `controller/` | Admin REST endpoints | `ShipmentRepublishController.java` (manual republish) |
| `resources/` | Config + SQL + WSDL | `application.properties` (env-var-driven), `wsdl/` (TM contract) |

## 5. Data Architecture

**Primary data stores**:
- **Kafka** — source of truth for inbound shipment events (no durable state here; Kafka retains events)
- **GCP Pub/Sub** — sink for all audit feedback (success + failure outcomes per event)
- **Application DB (JPA)** — small persistence layer for dock-door enrichment and cleanup state; schema managed via `resources/sql/`
- **Google Cloud Storage** — object store read-only by photo grabber

**Data flow pattern**: Stateless event processing. The service does not own the event log — it transforms and forwards. Only the dock-door cleanup path writes to a local DB.

**No migration strategy detected** (no Flyway/Liquibase); SQL scripts in `resources/sql/` likely applied manually or via CI.

## 6. API Surface

| Endpoint/Interface | Method | Purpose |
|-------------------|--------|---------|
| `/tms-shipment-load/actuator/health` | GET | Kubernetes liveness/readiness |
| `/tms-shipment-load/actuator/metrics` | GET | Prometheus scrape endpoint |
| `/tms-shipment-load/actuator/env` | GET | Config property inspection |
| `ShipmentRepublishController.*` | Various | Manual shipment event republish (admin) |
| `OrderTestController.*` | Various | Test order endpoint (dev/test only) |
| **Kafka consumer** (InboundShipment) | CONSUME | Shipment create/delete events |
| **Kafka consumer** (LTL) | CONSUME | LTL load tracking events |
| **Pub/Sub consumer** (ShipConfirm) | CONSUME | Appointment ship-confirm events |
| **TM REST API** (via TMRestApiClient) | POST/PUT | Outbound to Transportation Manager |
| **GCP Pub/Sub** (audit topics) | PUBLISH | Audit feedback for all event outcomes |

## 7. Key Abstractions

| Concept | Implementation | Files |
|---------|---------------|-------|
| Camel Route | `RouteBuilder` subclasses with `configure()` defining the DSL | `camel/routes/*.java` |
| Camel Processor | `Processor` implementations with `process(Exchange)` | `camel/processors/*.java` |
| Feature flag | `@ConditionalOnProperty(name="feature.X.enabled", havingValue="true")` | Most RouteBuilders |
| Kafka dual-mode | `kafkaMode` property switches between Managed and Confluent route configs | `InboundShipmentRouteBuilder.configureKafkaRoute()` |
| Audit feedback | `CommonUtil.publishAuditFeedbackToPubSub(feedback, payload)` | `util/CommonUtil.java` |
| TM JAXB types | Generated from WSDL; marshalled to XML for TM REST API | `dto/gen/transportationmanager/` |
| Exception hierarchy | Typed exceptions per failure mode → specific redelivery policies in routes | `exception/*.java` |
| CyberArk auth | SDK called at request time to retrieve TM API credentials | `client/TMRestApiClient.java` |

## 8. Dependency Analysis

**High-impact modules** (change carefully):
- `util/CommonUtil.java` — depended on by all 8 routes and most processors; used for audit publishing
- `client/TMRestApiClient.java` — single exit point to TM API; all services route through it
- `dto/gen/transportationmanager/` — JAXB-generated; changing the WSDL source breaks many compile-time usages across the codebase; do NOT hand-edit, regenerate from WSDL
- `mapper/LoadMapper.java` — complex JAXB-to-domain mapping; defects here cause silent data corruption

**Safe to modify independently**:
- Individual `camel/routes/*.java` — isolated by feature flag and event type
- Individual `camel/processors/*.java` — each handles one event type; they don't call each other
- `dto/inbound/*.java` — Kafka message DTOs, consumed only within their own processor chain
- `exception/*.java` — adding new exception types is safe

**No circular dependencies** detected.

## 9. Testing Landscape

- **Test framework**: Spring Boot Test + JUnit (from BOM)
- **Test file count**: ~38 test files (from profiler)
- **Test organization**: Mirrors main package; Camel route tests use `Edi990RouteBuilderTest`, etc.
- **Coverage gaps**: Route-level integration tests exist for some routes; processor-level unit tests sparse. The JAXB-generated DTO layer has no tests (expected — generated code).
- **Camel testing**: Uses Camel's test support (CamelSpringBootTest likely); no mock broker confirmed in test resources.

## 10. Build and Deployment

- **Build**: Maven 3.6.1 (`mvn clean package` → fat JAR via Spring Boot Maven plugin)
- **Parent POM**: `com.macys.msc.transportation:tms-shipment-load:1.0.0` — provides BOM for all dependency versions
- **Config pattern**: 100% env-var driven (`${ENV_VAR}` in application.properties). No hardcoded credentials.
- **Distribution**: Macys Nexus (internal Maven repo) for artifacts; `releaseDistributionLocaltion` and `snapshotDistributionLocaltion` env vars set repo URLs.
- **Kafka auth**: SSL truststore + SASL/PLAIN; env-var paths for keystore files
- **CyberArk**: `javapasswordsdk` called at runtime for TM API credentials — requires CyberArk agent running in deployment environment.

## 11. Technical Debt and Risks

| Area | Observation | Impact | Confidence |
|------|-------------|--------|------------|
| JAXB generated types | ~700 generated classes checked into source tree | High — regeneration needed if TM API contract changes; large surface area | High |
| No DB migration tool | SQL scripts in `resources/sql/` with no Flyway/Liquibase | Medium — schema drift risk in multi-env deployments | Medium |
| `OrderTestController` in production source | Test/debug controller in `src/main/java` (not test) | Low-Medium — may expose internal endpoints in prod | High |
| Camel version mix | `camel-google-pubsub-starter` appears twice with different versions (3.18.0 and 4.1.0) in pom.xml | High — version conflict risk at runtime | High |
| Hard-coded redelivery constants | `retryCount` and `retryInterval` from properties but default values vary per route | Low — operational inconsistency | Medium |
| `stock-screener/` subdirectory | Unrelated Python app committed in the same repo | Low — no cross-dependency; build noise | High |

## 12. Recommendations for Modification

- **Where to start**: Read `InboundShipmentRouteBuilder.java` and `CreateShipmentProcessor.java` to understand the core event flow pattern — all other routes follow the same shape.
- **Adding a new event type**: Create a new `RouteBuilder` + `Processor` + `Service` triple; wire via `@ConditionalOnProperty`; reuse `CommonUtil.publishAuditFeedbackToPubSub` for audit.
- **Be careful about**: The JAXB `dto/gen/transportationmanager/` package — treat as a black box, regenerate from WSDL only. `TMRestApiClient` CyberArk auth path — changes break all outbound TM calls.
- **What to test after changes**: Any change to `CommonUtil`, `LoadMapper`, or `TMRestApiClient` requires running all route integration tests. Processor changes only need that processor's tests.
- **Potential pitfalls**: The duplicate `camel-google-pubsub-starter` versions in pom.xml should be resolved before adding any Pub/Sub functionality. The `OrderTestController` should be moved to test scope or removed before shipping to prod.
