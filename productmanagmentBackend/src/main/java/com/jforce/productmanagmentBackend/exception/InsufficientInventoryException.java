package com.jforce.productmanagmentBackend.exception;

public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(String productName, int requested, int available) {
        super("Insufficient inventory for '" + productName + "'. Requested: " + requested + ", Available: " + available);
    }
}
