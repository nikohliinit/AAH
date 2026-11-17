package com.macys.logistics.shipment.load.exception;

public class TmsLoadValidationException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TmsLoadValidationException() {
        super();
    }

    public TmsLoadValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TmsLoadValidationException(String message) {
        super(message);
    }

    public TmsLoadValidationException(Throwable cause) {
        super(cause);
    }
}
