package com.macys.logistics.shipment.load.exception;

/**
 * Exception thrown when processing an EDI 990 acceptance or decline message fails.
 * This includes transient processing failures during business logic execution
 * or downstream service errors that may be retried.
 */
public class Edi990ProcessingException extends RuntimeException {

    /**
     * Constructs a new Edi990ProcessingException with the specified detail message.
     *
     * @param message the detail message
     */
    public Edi990ProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs a new Edi990ProcessingException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public Edi990ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
