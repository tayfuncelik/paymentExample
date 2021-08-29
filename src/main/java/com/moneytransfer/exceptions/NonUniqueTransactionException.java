package com.moneytransfer.exceptions;

public class NonUniqueTransactionException extends Exception {
  private static final long serialVersionUID = 1L;

  public NonUniqueTransactionException(String message) {
    super(message);
  }
}
