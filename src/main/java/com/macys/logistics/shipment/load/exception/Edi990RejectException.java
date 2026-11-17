package com.macys.logistics.shipment.load.exception;

/**
 * Exception thrown when an EDI 990 message with responseCode "R" (Reject) is received.
 * These messages are routed directly to the Dead Letter Queue for separate handling.
 */
public class Edi990RejectException extends RuntimeException {

    /**
     * Constructs a new Edi990RejectException with the specified detail message.
     *
     * @param message the detail message
     */
    public Edi990RejectException(String message) {
        super(message);
    }

    /**
     * Constructs a new Edi990RejectException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public Edi990RejectException(String message, Throwable cause) {
        super(message, cause);
    }
}
