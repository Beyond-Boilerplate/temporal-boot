package com.github.sardul3.temporal_boot.app.exceptions;

public class TransactionCancelledException extends RuntimeException {
    public TransactionCancelledException() {
        super("Transaction was cancelled");
    }
}
