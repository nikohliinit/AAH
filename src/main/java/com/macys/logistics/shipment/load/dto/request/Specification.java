package com.macys.logistics.shipment.load.dto.request;

import java.util.Arrays;
import java.util.Optional;

public enum Specification {

    PALLETIZED_PREAPPROVED_TL_IM(3, "Palletized: Preapproved  TL/IM"),
    DRIVER_LOAD(4, "Driver Load"),
    VENDOR_LOAD(5, "Vendor Load"), PRODUCT_IS_JIFFY_WRAPPED(6, "Product is Jiffy wrapped"),
    PRODUCT_IS_BLANKET_WRAPPED(7, "Product is Blanket wrapped"),
    ONE_DAY_ADVANCE_PICKUP_NOTICE_REQ(10, "24hr Advance Pickup Notice Req"),
    BY_APPOINTMENT_ONLY(13, "By Appointment only"),
    NEED_VND_PICKUP_CONTACT_MTO(14, "Need Vnd Pickup #- Contact MTO"),
    THIRD_PARTY_BILL_CONTACT_VENDO(16, "Third Party Bill-contact vendo"),
    PRE_ASSIGNED_MTO_APPROVAL_REQ(17, "Pre-Assigned: MTO Approval Req"),
    PRE_LOADED_MTO_APPROVAL_REQ(18, "Pre-Loaded: MTO Approval Req"), AIR_REQUEST(20, "Air Request"),
    LOAD_LOCKS_REQUIRED(21, "Load Locks Required"), FLOOR_SAMPLES(24, "Floor Samples"),
    PHOTO_SAMPLES(29, "Photo Samples"), VENDOR_LOAD_DRIVER_COUNT(31, "Vendor Load/Driver Count"),
    POE_SHIPMENT(42, "POE Shipment"), EXPENSE_ITEMS(44, "Expense Items"), UNKNOWN(-1, "UNKNOWN");

    private Integer specNbr;
    private String specDescription;

    Specification(Integer specNbr, String specDescription) {
        this.specNbr = specNbr;
        this.specDescription = specDescription;
    }

    public Integer getSpecNbr() {
        return specNbr;
    }

    public String getSpecDescription() {
        return specDescription;
    }

    public static Specification freightBillSpecBySpecNbr(Integer specNbr) {
        Optional<Specification> specOpt = Arrays.stream(Specification.values())
                .filter(n -> n.getSpecNbr() == specNbr).findAny();
        return specOpt.isPresent() ? specOpt.get() : UNKNOWN;
    }

}
