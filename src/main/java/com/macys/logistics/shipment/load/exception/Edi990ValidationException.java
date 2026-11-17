package com.macys.logistics.shipment.load.exception;

/**
 * Exception thrown when an EDI 990 message fails validation.
 * This includes malformed JSON, deserialization failures, missing required fields,
 * or invalid responseCode values.
 */
public class Edi990ValidationException extends RuntimeException {

    /**
     * Constructs a new Edi990ValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public Edi990ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new Edi990ValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public Edi990ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
