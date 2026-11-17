package com.macys.logistics.shipment.load.exception;

public class TmsAuditMessagePublishException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TmsAuditMessagePublishException() {
        super();
    }

    public TmsAuditMessagePublishException(String message, Throwable cause) {
        super(message, cause);
    }

    public TmsAuditMessagePublishException(String message) {
        super(message);
    }

    public TmsAuditMessagePublishException(Throwable cause) {
        super(cause);
    }
}