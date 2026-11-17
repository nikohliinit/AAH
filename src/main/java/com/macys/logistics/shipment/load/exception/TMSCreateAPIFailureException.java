package com.macys.logistics.shipment.load.exception;

public class TMSCreateAPIFailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TMSCreateAPIFailureException(String message) {
        super(message);
    }

    public TMSCreateAPIFailureException(String message, Throwable t) {
        super(message, t);
    }

    public TMSCreateAPIFailureException(Throwable t) {
        super(t);
    }

}
