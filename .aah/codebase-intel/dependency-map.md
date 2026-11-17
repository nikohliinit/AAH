# Dependency Map

## External Dependencies

| Package | Version | Purpose | Category |
|---------|---------|---------|----------|
| spring-boot-starter-web | BOM | REST endpoints, embedded Tomcat | Framework |
| spring-boot-starter-actuator | BOM | Health, metrics, env endpoints | Observability |
| spring-boot-starter-data-jpa | BOM | Persistence layer (dock-door, enrichment) | Data |
| spring-boot-starter-validation | BOM | Bean Validation (Jakarta) | Validation |
| spring-boot-starter-thymeleaf | BOM | HTML templates | Presentation |
| spring-boot-starter-log4j2 | BOM | Async structured logging | Logging |
| spring-retry | BOM | @EnableRetry + @Retryable on services | Reliability |
| spring-aspects | BOM | AOP support for Spring Retry | Framework |
| camel-kafka-starter | 4.1.0 | Kafka consumer routes (Managed + Confluent) | Messaging |
| camel-google-pubsub-starter | 4.1.0 | GCP Pub/Sub routes (audit feedback) | Messaging |
| camel-google-storage | 4.0.0 | GCS access (photo grabber) | Storage |
| camel-core | Camel BOM | Camel DSL, exchange, processor API | Integration |
| camel-spring | Camel BOM | Spring-Camel integration | Integration |
| camel-http | Camel BOM | HTTP component for Camel routes | Integration |
| camel-jms | Camel BOM | JMS component (legacy routes if any) | Messaging |
| camel-jackson | Camel BOM | JSON marshalling in Camel routes | Serialization |
| camel-quartz | Camel BOM | Scheduled route triggers | Scheduling |
| google-cloud-secretmanager | 2.23.0 | GCP Secret Manager access | Security |
| javapasswordsdk (CyberArk) | BOM | CyberArk runtime credential retrieval | Security |
| micrometer-tracing | 1.3.5 | Distributed tracing abstraction | Observability |
| micrometer-tracing-bridge-otel | 1.3.5 | OpenTelemetry bridge | Observability |
| lombok | BOM | @Data, @Log4j2, boilerplate reduction | Dev Tools |
| jakarta.xml.bind-api | Jakarta EE | JAXB annotation and marshal API | Serialization |
| jakarta.jms-api | Jakarta EE | JMS API types | Messaging |
| jakarta.ws.rs-api | Jakarta EE | JAX-RS types | HTTP |
| jakarta.xml.soap-api | Jakarta EE | SOAP/SAAJ API (WSDL stubs) | Integration |
| commons-io | 2.11.0 | File/stream I/O utilities | Utilities |
| commons-codec | BOM | Base64, hex encoding | Utilities |
| commons-lang3 | BOM | StringUtils, EnumUtils, ObjectUtils | Utilities |
| disruptor (LMAX) | BOM | High-performance async logging queue | Logging |
| springdoc-openapi-starter-webmvc-ui | BOM | Swagger UI | API Docs |

## Internal Module Dependencies

| Module | Depends On | Depended By |
|--------|-----------|-------------|
| `camel.routes.*` | `camel.processors.*`, Spring `@Value` config | (entry point — nothing depends on routes) |
| `camel.processors.*` | `service.*`, `dto.*`, `exception.*`, `util.CommonUtil` | `camel.routes.*` |
| `service.*` | `client.*`, `dto.*`, `mapper.*`, `repository.*`, `rule.*` | `camel.processors.*` |
| `client.TMRestApiClient` | `dto.gen.transportationmanager.*`, Spring RestTemplate, CyberArk SDK | `service.*` |
| `dto.gen.transportationmanager.*` | JAXB runtime (jakarta.xml.bind) | `client.*`, `mapper.*`, `camel.processors.*` |
| `dto.inbound.*` | (plain POJOs) | `camel.processors.*`, `service.*` |
| `mapper.LoadMapper` | `dto.gen.transportationmanager.*`, `dto.enrichment.*` | `service.*`, `camel.processors.*` |
| `util.CommonUtil` | `dto.AuditFeedback`, GCP Pub/Sub camel route | `camel.processors.*`, `camel.routes.*` |
| `exception.*` | (plain exceptions) | `camel.routes.*` (exception handlers), `camel.processors.*` |
| `logging.ShipmentLoadIntegrationMetricsHelper` | Micrometer `MeterRegistry` | `camel.processors.*` |
| `rule.*` | `dto.*` | `service.*` |
| `config.*` | Spring framework | `ShipmentLoadApplication` |
| `controller.*` | `service.*`, Spring MVC | (REST API — independent of route layer) |
