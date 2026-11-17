package com.macys.logistics.shipment.load.exception;

public class TMSDataValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TMSDataValidationException(String message) {
        super(message);
    }

    public TMSDataValidationException(String message, Throwable t) {
        super(message, t);
    }

    public TMSDataValidationException(Throwable t) {
        super(t);
    }
}
