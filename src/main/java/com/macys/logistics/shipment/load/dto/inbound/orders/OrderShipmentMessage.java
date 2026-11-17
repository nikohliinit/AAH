package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderShipmentMessage implements Serializable {

    @JsonProperty("transCode")
    private String transCode;

    @JsonProperty("fbNbr")
    private String fbNbr;

    @JsonProperty("order")
    private Order order;

    @JsonProperty("specs")
    private List<Spec> specs;

    @JsonProperty("specialRouteRequests")
    private List<SpecialRouteRequest> specialRouteRequests;

    @JsonProperty("notes")
    private List<Note> notes;

    @JsonProperty("audit")
    private Audit audit;

    // --- Enrichment fields populated by upstream (DB enrichment service / Pub/Sub producer) ---
    // These mirror the 4 extra input parameters passed to TIBCO Create.process by SendShipmentToJDA.process

    /** TIBCO enrichment: PreferredAPCarrierCode — derived from special route business rules */
    @JsonProperty("preferredAPCarrierCode")
    private String preferredAPCarrierCode;

    /** TIBCO enrichment: SPCL_RTE_NBR — special route number from DB query on SHPMT_SPCL_RTE_RQST */
    @JsonProperty("spclRteNbr")
    private String spclRteNbr;

    /** TIBCO enrichment: PrimaryLoc — overrides ShipFromLocationCode when present */
    @JsonProperty("primaryLoc")
    private String primaryLoc;

    /** TIBCO enrichment: VendorName — added as 'VendorName' reference number to TMS payload */
    @JsonProperty("vendorName")
    private String vendorName;
}
