package com.macys.logistics.shipment.load.dto.inbound.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Order implements Serializable {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("origin")
    private LocationInfo origin;

    @JsonProperty("destination")
    private LocationInfo destination;

    @JsonProperty("pickupWindow")
    private TimeWindow pickupWindow;

    @JsonProperty("deliveryWindow")
    private TimeWindow deliveryWindow;

    @JsonProperty("charges")
    private Charges charges;

    @JsonProperty("carrier")
    private Carrier carrier;

    @JsonProperty("vendor")
    private Vendor vendor;

    @JsonProperty("commodity")
    private Commodity commodity;

    @JsonProperty("lineItems")
    private List<LineItem> lineItems;

    @JsonProperty("references")
    private List<Reference> references;

    @JsonProperty("profiles")
    private List<String> profiles;

    // --- Fields mapped from TIBCO Header fields (not reference entries) ---

    /** TIBCO: Header/PASS_CHAR1 → TMS Shipment.CustomerCode */
    @JsonProperty("customerCode")
    private String customerCode;

    /** TIBCO: Header/FREIGHT_TERMS → mapped to TMS FreightTermsEnumVal (COL→FT_COLLECT, PPD→FT_PRE_PAID, else→FT_NULL) */
    @JsonProperty("freightTerms")
    private String freightTerms;

    /** TIBCO: Header/SHIP_PROJECT → TMS mergeInTransitConsolidationCode + PROJ_ID reference number */
    @JsonProperty("shipProject")
    private String shipProject;
}
