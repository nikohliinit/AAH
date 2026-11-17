package com.macys.logistics.shipment.load.exception;

/**
 * A customized exception class to handle update appointment ship confirm specific exceptions.
 */
public class ShipConfirmProcessingException extends RuntimeException {

    public ShipConfirmProcessingException(Throwable cause) {
        super(cause);
    }

    public ShipConfirmProcessingException(String message) {
        super(message);
    }

    public ShipConfirmProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
