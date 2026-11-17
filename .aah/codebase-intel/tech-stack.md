# Technology Stack

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| Language | Java | 21 | Primary runtime language |
| Framework | Spring Boot | (parent BOM) | Application container, DI, REST, JPA |
| Integration | Apache Camel | 4.1.0 | Event routing, processor pipeline, exception handling |
| Messaging | Apache Kafka (Managed) | via camel-kafka-starter 4.1.0 | Shipment event consumption (VSP managed cluster) |
| Messaging | Apache Kafka (Confluent) | via camel-kafka-starter 4.1.0 | Shipment event consumption (Confluent Cloud) |
| Messaging | Google Cloud Pub/Sub | camel-google-pubsub 4.1.0 | Audit feedback publishing and ship-confirm intake |
| Object Storage | Google Cloud Storage | camel-google-storage 4.0.0 | Photo/image asset retrieval |
| Secrets | CyberArk Password SDK | (javapasswordsdk) | Runtime credential retrieval (TM API auth) |
| Secret Manager | Google Cloud Secret Manager | 2.23.0 | GCP secret access |
| HTTP Client | Spring RestTemplate | Spring Boot BOM | Outbound calls to Transportation Manager REST API |
| Data Access | Spring Data JPA | Spring Boot BOM | Persistence layer (dock-door, enrichment entities) |
| XML Binding | JAXB (Jakarta XML Bind) | Jakarta EE | Mapping JAXB-generated TM API DTOs |
| Build | Maven | 3.6.1 | Build, dependency management, multi-module parent |
| Logging | Log4j2 + Log4j-spring-boot | (Spring Boot BOM) | Structured async logging (LMAX disruptor) |
| Observability | Micrometer + OpenTelemetry | 1.3.5 | Distributed tracing (bridge to OTEL) |
| Observability | Micrometer / Prometheus | Spring Boot Actuator | Metrics scraping endpoint |
| API Docs | SpringDoc OpenAPI | (springdoc-openapi) | Swagger UI at /swagger-ui |
| Utilities | Lombok | (Spring Boot BOM) | Boilerplate reduction (@Data, @Log4j2) |
| Utilities | Apache Commons IO | 2.11.0 | I/O utilities |
| Utilities | Apache Commons Lang3 | (BOM) | String, enum, object utilities |
| Scheduling | Camel Quartz | (Camel BOM) | Scheduled route triggers (dock-door cleanup) |
| Validation | Spring Validation (Jakarta) | Spring Boot BOM | Request validation |
| Retry | Spring Retry | Spring Boot BOM | @EnableRetry on application, route-level redelivery |
| Testing | Spring Boot Test + JUnit | Spring Boot BOM | Unit and integration test framework |
| Dev Tools | Java Thymeleaf | Spring Boot BOM | HTML templates (error pages / reporting) |
