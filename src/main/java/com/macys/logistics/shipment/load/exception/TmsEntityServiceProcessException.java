package com.macys.logistics.shipment.load.exception;

public class TmsEntityServiceProcessException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public TmsEntityServiceProcessException() {
        super();
    }

    public TmsEntityServiceProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public TmsEntityServiceProcessException(String message) {
        super(message);
    }

    public TmsEntityServiceProcessException(Throwable cause) {
        super(cause);
    }

}
