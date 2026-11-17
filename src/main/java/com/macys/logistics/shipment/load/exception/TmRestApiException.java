package com.macys.logistics.shipment.load.exception;

public class TmRestApiException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TmRestApiException() {
		super();
	}

	public TmRestApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public TmRestApiException(String message) {
		super(message);
	}

	public TmRestApiException(Throwable cause) {
		super(cause);
	}
	
	

}
