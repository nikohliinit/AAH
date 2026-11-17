package com.macys.logistics.shipment.load.exception;

public class TMSMappingException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TMSMappingException(String message) {
        super(message);
    }

    public TMSMappingException(String message, Throwable t) {
        super(message, t);
    }

    public TMSMappingException(Throwable t) {
        super(t);
    }
}
