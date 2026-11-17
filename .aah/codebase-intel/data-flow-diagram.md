# Data Flow

## Overview
Data enters as JSON events on Kafka topics (Managed or Confluent) or Google Pub/Sub, flows through a Camel route for routing and exception policy, through a typed Processor for validation and orchestration, into a Service for business logic and downstream API calls, and exits as REST calls to Transportation Manager. Audit feedback for all event outcomes is published back to GCP Pub/Sub.

## Data Flow Diagram

```mermaid
flowchart TD
    subgraph "Inbound — Shipment Events"
        MK[[Managed Kafka<br/>shipment topic]] --> ISR
        CK[[Confluent Kafka<br/>confluent shipment topic]] --> ISR
        ISR[InboundShipmentRouteBuilder] --> SEP[ShipmentEventProcessor<br/>discriminate CREATE vs DELETE]
        SEP -->|CREATE| CSP[CreateShipmentProcessor]
        SEP -->|DELETE| DSP[DeleteShipmentProcessor]
        CSP & DSP --> ISS[InBoundShipmentService]
    end

    subgraph "Inbound — Ship Confirm"
        PSC[[GCP Pub/Sub<br/>AppointmentShipConfirm]] --> SCR[ShipConfirmRouteBuilder]
        SCR -->|DIGITAL + SHIP_CONFIRM + OBTM| SCP[ShipConfirmProcessor]
        SCP --> LES[LoadEnrichmentService]
    end

    subgraph "Inbound — LTL Events"
        LTL_K[[Kafka/Route<br/>supplychain.tmsintg.ltl]] --> LER[LTLEventRouteBuilder]
        LER --> LEP[LTLLoadEventProcessor]
        LEP --> LTS[LTLTrackingMessageService]
    end

    subgraph "Inbound — Trailer Dock/Image"
        CDC_K[[Kafka/Route<br/>TrailerDoorDockCDC]] --> TDR[TrailerDoorDockCdcRouteBuilder]
        TDR --> TDP[TrailerDoorDockCdcEventProcessor]
        TDP --> TDS2[TrailerDoorDockCdcEventService]

        IMG_K[[Kafka/Route<br/>TrailerImageAttach]] --> TIR[TrailerImageAttachRouteBuilder]
        TIR --> TIP[TrailerImageAttachProcessor]
        TIP --> TDS[TrailerDockDoorService]
    end

    subgraph "Inbound — Photo / Orders / TM Update"
        PGR[PhotoGrabberRouteBuilder] --> PGP[PhotoGrabberProcessor]
        PGP --> PGS[PhotoGrabberService]
        PGP -.->|GCS download| GCS[(Google Cloud Storage)]

        OR[OrdersRouteBuilder] --> OHP[OrdersHubCheckProcessor]
        OHP --> OS[OrderService]

        UTR[UpdateTMRouteBuilder] --> UTP[UpdateTMProcessor]
        UTP --> LES2[LoadEnrichmentService]
    end

    subgraph "Outbound — TM API"
        ISS & LTS & LES & LES2 & TDS & TDS2 & PGS & OS --> RC[TMRestApiClient]
        RC -->|CyberArk token| CA[CyberArk Vault]
        CA -.->|credentials| RC
        RC -->|REST / JAXB-XML| TM[Transportation Manager API]
    end

    subgraph "Outbound — Audit Feedback"
        ISR & SCR & LER & TDR & TIR --> CU[CommonUtil.publishAuditFeedback]
        CU -->|success + failure| PS_AUDIT[[GCP Pub/Sub<br/>audit.feedback topics]]
    end
```

## Data Pipelines

| Pipeline | Source | Processing | Destination |
|----------|--------|-----------|-------------|
| Shipment Create | Kafka (shipment topic) | JSON → ShipmentEnterpriseMessage DTO → validate → InBoundShipmentService | TM REST API (create shipment) |
| Shipment Delete | Kafka (shipment topic) | JSON → ShipmentEnterpriseMessage → DeleteShipmentProcessor | TM REST API (delete shipment) |
| Ship Confirm | GCP Pub/Sub | JSON → header filter (DIGITAL+SHIP_CONFIRM+OBTM) → ShipConfirmProcessor | TM REST API (update appointment) |
| LTL Load Event | Kafka (LTL topic) | LTL message → LTLLoadEventProcessor → LTLTrackingMessageService | TM REST API |
| Trailer Dock CDC | Kafka/CDC | CDC event → TrailerDoorDockCdcEventProcessor | TM REST API (dock/door update) |
| Trailer Image | Kafka | Image event → TrailerImageAttachProcessor → TM + GCS | TM REST API (image attach) |
| Photo Grabber | Scheduled/Route | GCS photo download → PhotoGrabberProcessor | TM REST API (photo update) |
| Audit Feedback | All routes (error/success) | CommonUtil → AuditFeedback DTO | GCP Pub/Sub (audit topics) |
| Dock Door Cleanup | Quartz schedule | DockDoorCleanupProcessorService | DB / TM API |

## Integration Points

- **Inbound**: Kafka (Managed Kafka VSP cluster + Confluent Cloud) for shipment/LTL/trailer events
- **Inbound**: GCP Pub/Sub for ship-confirm appointment events
- **Outbound**: Transportation Manager REST API (JAXB XML-bound request/response)
- **Outbound**: GCP Pub/Sub audit feedback topics (3 separate topic configs: shipment, trailer image, dock-door)
- **Outbound**: Google Cloud Storage (photo retrieval)
- **Auth**: CyberArk Password SDK called at request time for TM API credentials
- **Config**: GCP Secret Manager for GCP-managed secrets
