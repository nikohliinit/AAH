package com.macys.logistics.shipment.load.dto.request;

public enum SRRCodeEnum {
    POE_COLLECT_CONTAINER("111"),
    COLLECT_PRE_ASSIGNED_CARRIER("122"),
    POE_PREPAID_CONTAINER("134"),
    NEED_APPOINTMENT_ONLY_PREPAID("144"),
    COLLECT_VIP_1_TO_1("177"),
    DC_BY_PASS_COLLECT_CONTAINER ("180"),
    DC_BY_PASS_PRE_PAID_CONTAINER("190"),
    NONE("200");


    public final String label;

    private SRRCodeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    public static SRRCodeEnum getByLabel(String label) {
        for (SRRCodeEnum value : SRRCodeEnum.values()) {
            if (value.getLabel().equals(label)) {
                return value;
            }
        }
        // Handle the case when the label is not found
        throw new IllegalArgumentException("No enum constant with label: " + label);
    }
}
