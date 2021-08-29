package com.moneytransfer.exceptions;

public class ValidationException {
    public static class NullException extends Exception {
        private static final long serialVersionUID = 1l;

        public NullException(String message) {
            super(message);
        }
    }

    public static class InsufficientBalanceException extends Exception {
        private static final long serialVersionUID = 1l;

        public InsufficientBalanceException(String message) {
            super(message);
        }
    }
}
