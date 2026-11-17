package com.macys.logistics.shipment.load.exception;

public class TMSDeleteAPIFailureException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TMSDeleteAPIFailureException(String message, Throwable t) {
    super(message, t);
  }

  public TMSDeleteAPIFailureException(Throwable t) {
    super(t);
  }
}
