package com.macys.logistics.shipment.load.enums;

public enum Event {

    SHIP_CONFIRM("SHPSHPD");

    private final String type;

    Event(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
