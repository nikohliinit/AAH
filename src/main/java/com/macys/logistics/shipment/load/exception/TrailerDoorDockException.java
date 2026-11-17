package com.macys.logistics.shipment.load.exception;

public class TrailerDoorDockException extends Exception {

    private static final long serialVersionUID = 1L;

    public TrailerDoorDockException() {
        super();
    }

    public TrailerDoorDockException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrailerDoorDockException(String message) {
        super(message);
    }

    public TrailerDoorDockException(Throwable cause) {
        super(cause);
    }

}

