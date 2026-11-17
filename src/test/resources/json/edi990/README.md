# EDI 990 Test Payload Samples

This directory contains sample JSON payloads for testing EDI 990 (Response to Load Tender) message processing.

## Files

### Valid Payloads

- **edi990-accept-valid.json**: Valid acceptance message (responseCode = "A") with equipment details, weight information, and reference identifications
- **edi990-reject-valid.json**: Valid reject message (responseCode = "R") that should be routed to DLQ
- **edi990-decline-valid.json**: Valid decline message (responseCode = "D") with reason information

### Invalid/Error Payloads

- **edi990-invalid-response-code.json**: Message with invalid responseCode "X" (should throw Edi990ValidationException)
- **edi990-missing-fields.json**: Message missing required fields like shipmentIdentificationNumber and responseCode
- **edi990-malformed.json**: Malformed JSON with syntax errors (double comma)
- **edi990-missing-message-id.json**: Message with null/missing messageId field
- **edi990-null-data.json**: Message with null data section

## Usage in Tests

These payloads can be loaded in tests using:

```java
String payload = new String(Files.readAllBytes(
    Paths.get("src/test/resources/json/edi990/edi990-accept-valid.json")));
```

Or use the DataFactory methods:
```java
String acceptPayload = DataFactory.createEdi990AcceptJsonPayload();
String declinePayload = DataFactory.createEdi990DeclineJsonPayload();
```

## Response Codes

- **A**: Accept - Carrier accepts the load tender
- **D**: Decline - Carrier declines the tender (provides reason)
- **R**: Reject - Message rejected, routed to DLQ for manual review

## Reference

Based on EDI 990 standard structure with fields mapped from actual usage documented in:
- `/edi990-accepted-payload.json`
- `/edi990-rejected-payload.json`
- `/edi990-schema.json`
