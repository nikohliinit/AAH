package com.macys.logistics.shipment.load.exception;

public class TmsLoadProcessException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TmsLoadProcessException() {
        super();
    }

    public TmsLoadProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public TmsLoadProcessException(String message) {
        super(message);
    }

    public TmsLoadProcessException(Throwable cause) {
        super(cause);
    }
}
