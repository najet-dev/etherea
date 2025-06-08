package com.etherea.dtos;

import com.etherea.enums.PaymentStatus;
public class PaymentResponseDTO {
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String clientSecret;
    public PaymentResponseDTO(PaymentStatus paymentStatus, String transactionId, String clientSecret) {
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.clientSecret = clientSecret;
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
    public String getClientSecret() {
        return clientSecret;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
