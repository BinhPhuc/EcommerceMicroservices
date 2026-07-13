package com.binhphuc.product_service.exception;

public class ResourceExistException extends RuntimeException {
  public ResourceExistException(String message) {
    super(message);
  }
}
