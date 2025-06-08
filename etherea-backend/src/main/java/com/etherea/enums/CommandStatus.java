package com.etherea.enums;

public enum CommandStatus {
    PENDING,         // Order created, awaiting payment
    PAID,             // Payment confirmed
    PROCESSING,      // Order in progress
    SHIPPED,         // Order shipped
    DELIVERED,       // Order delivered
    CANCELLED        // Order cancelled
}
