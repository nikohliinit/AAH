package com.macys.logistics.shipment.load.enums;

public enum Order {

    DIGITAL("Digital"),
    MOVEMENT("Movement");

    private final String type;

    Order(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
