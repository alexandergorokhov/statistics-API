package com.n26.exception;

public class InFutureTransactionException extends RuntimeException {
    public InFutureTransactionException(String message) {
        super(message);
    }
}
