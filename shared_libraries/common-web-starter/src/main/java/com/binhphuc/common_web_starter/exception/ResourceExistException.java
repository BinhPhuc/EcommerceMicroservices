package com.binhphuc.common_web_starter.exception;

public class ResourceExistException extends RuntimeException {
  public ResourceExistException(String message) {
    super(message);
  }
}
