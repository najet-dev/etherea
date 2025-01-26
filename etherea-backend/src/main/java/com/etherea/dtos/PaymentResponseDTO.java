package com.etherea.dtos;

import com.etherea.enums.PaymentStatus;

public class PaymentResponseDTO {
    private PaymentStatus paymentStatus;
    private String transactionId;
    public PaymentResponseDTO(PaymentStatus paymentStatus, String transactionId) {
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
    }
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
