package com.tcmj.pug.enums.model;

/** This exception will be used to wrap several checked exceptions to a RuntimeException. */
public class ClassCreationException extends RuntimeException {
  public ClassCreationException(Throwable cause) {
    super(cause);
  }

  public ClassCreationException(String text) {
    super(text);
  }
}
