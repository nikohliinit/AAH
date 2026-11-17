# Codebase Structure

## Directory Layout

```
service/                                    # project root
├── pom.xml                                 # Maven module POM (child of tms-shipment-load parent)
├── CLAUDE.md                               # AAH delivery ruleset
├── .aah/                                   # AAH project metadata
│   ├── manifest.yaml
│   ├── claude-progress.json
│   └── codebase-intel/                     # intelligence artifacts
├── src/
│   ├── main/
│   │   ├── java/com/macys/logistics/shipment/load/
│   │   │   ├── ShipmentLoadApplication.java        # Spring Boot entry point
│   │   │   ├── camel/
│   │   │   │   ├── routes/                          # Camel RouteBuilders (8 routes)
│   │   │   │   │   ├── InboundShipmentRouteBuilder.java
│   │   │   │   │   ├── ShipConfirmRouteBuilder.java
│   │   │   │   │   ├── LTLEventRouteBuilder.java
│   │   │   │   │   ├── TrailerDoorDockCdcRouteBuilder.java
│   │   │   │   │   ├── TrailerImageAttachRouteBuilder.java
│   │   │   │   │   ├── PhotoGrabberRouteBuilder.java
│   │   │   │   │   ├── OrdersRouteBuilder.java
│   │   │   │   │   └── UpdateTMRouteBuilder.java
│   │   │   │   └── processors/                     # Camel Processors (10 processors)
│   │   │   │       ├── ShipmentEventProcessor.java  # event type discriminator
│   │   │   │       ├── CreateShipmentProcessor.java
│   │   │   │       ├── DeleteShipmentProcessor.java
│   │   │   │       ├── ShipConfirmProcessor.java
│   │   │   │       ├── LTLLoadEventProcessor.java
│   │   │   │       ├── TrailerDoorDockCdcEventProcessor.java
│   │   │   │       ├── TrailerImageAttachProcessor.java
│   │   │   │       ├── PhotoGrabberProcessor.java
│   │   │   │       ├── OrdersHubCheckProcessor.java
│   │   │   │       ├── UpdateTMProcessor.java
│   │   │   │       └── MicrometerUtility.java
│   │   │   ├── service/                             # Business logic (interface + impl pairs)
│   │   │   │   ├── InBoundShipmentService[Impl].java
│   │   │   │   ├── LTLTrackingMessageService[Impl].java
│   │   │   │   ├── LoadEnrichmentService[Impl].java
│   │   │   │   ├── TrailerDockDoorService[Impl].java
│   │   │   │   ├── TrailerDoorDockCdcEventService[Impl].java
│   │   │   │   ├── DockDoorCleanupProcessorService[Impl].java
│   │   │   │   ├── PhotoGrabberService[Impl].java
│   │   │   │   ├── OrderService[Impl].java
│   │   │   │   └── LocationServiceClient[Impl].java
│   │   │   ├── client/                              # HTTP clients
│   │   │   │   ├── TMRestApiClient.java             # TM API (JAXB-typed requests)
│   │   │   │   ├── TmRestApiService.java
│   │   │   │   ├── RestApiClient.java
│   │   │   │   ├── AuthTokenResponse.java
│   │   │   │   └── HasResponseHeader.java
│   │   │   ├── dto/                                 # Data Transfer Objects
│   │   │   │   ├── inbound/                         # Kafka message DTOs
│   │   │   │   ├── gen/transportationmanager/       # JAXB-generated TM API types (~700 classes)
│   │   │   │   ├── enrichment/                      # Load enrichment DTOs
│   │   │   │   ├── shipconfirm/                     # Ship confirm DTOs
│   │   │   │   ├── dockdoor/                        # Dock door DTOs
│   │   │   │   ├── request/ & response/             # Generic request/response wrappers
│   │   │   │   └── *.java                           # Top-level shared DTOs
│   │   │   ├── mapper/                              # Data mappers (JAXB ↔ domain)
│   │   │   │   └── LoadMapper.java
│   │   │   ├── repository/                          # (empty — JPA repos likely in parent)
│   │   │   ├── config/                              # Spring config beans
│   │   │   │   ├── LogInterceptor.java
│   │   │   │   └── RestTemplateResponseErrorHandler.java
│   │   │   ├── controller/                          # REST endpoints (admin/test use)
│   │   │   │   ├── OrderTestController.java
│   │   │   │   └── ShipmentRepublishController.java
│   │   │   ├── enums/                               # Event, Order, Status enums
│   │   │   ├── exception/                           # Typed exception hierarchy
│   │   │   ├── logging/                             # Metrics helper (Micrometer)
│   │   │   ├── rule/                                # Business rule evaluators
│   │   │   └── util/                                # Constants, CommonUtil
│   │   └── resources/
│   │       ├── application.properties               # Main config (env-var-driven)
│   │       ├── application-local.properties         # Local overrides
│   │       ├── application-mdc.properties           # MDC logging config
│   │       ├── config/                              # Additional Spring config files
│   │       ├── rules/                               # Rule definitions
│   │       ├── sql/                                 # SQL scripts
│   │       ├── templates/                           # Thymeleaf templates
│   │       └── wsdl/                               # WSDL definitions (TM SOAP stubs)
│   └── test/
│       └── java/com/macys/logistics/shipment/load/  # Test classes (JUnit/Camel test)
└── stock-screener/                                  # Unrelated Python utility (separate app)
```

## Entry Points

| File | Role | Description |
|------|------|-------------|
| `ShipmentLoadApplication.java` | Spring Boot main | Bootstraps Spring context, registers RestTemplate, enables retry |
| `InboundShipmentRouteBuilder.java` | Primary Camel route | Kafka consumer → shipment event dispatch |
| `LTLEventRouteBuilder.java` | LTL Camel route | LTL event consumer |
| `UpdateTMRouteBuilder.java` | TM update route | Appointment/load update dispatcher |

## API Routes

| Route | Method | Handler | Description |
|-------|--------|---------|-------------|
| `/tms-shipment-load/actuator/health` | GET | Spring Actuator | Health check |
| `/tms-shipment-load/actuator/metrics` | GET | Spring Actuator | Prometheus metrics |
| `/tms-shipment-load/actuator/env` | GET | Spring Actuator | Environment properties |
| (OrderTestController) | Various | `OrderTestController` | Test-only order endpoints |
| (ShipmentRepublishController) | Various | `ShipmentRepublishController` | Manual republish trigger |

## Module Organization

The codebase follows a **Layer-first** organization: `camel/routes → camel/processors → service → client → dto`. Each Camel route owns one event type end-to-end: its RouteBuilder, one or more Processors, one or more Services, and the shared TMRestApiClient sink. The `dto/gen/transportationmanager/` package (~700 JAXB-generated classes) is generated from WSDL/XSD and is not hand-edited.
