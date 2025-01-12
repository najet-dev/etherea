package com.etherea.dtos;

public class PaymentResponse {

    private String status;
    private String chargeId;  // Stripe payment ID
    private String message;
    public PaymentResponse() {}
    public PaymentResponse(String status, String chargeId, String message) {
        this.status = status;
        this.chargeId = chargeId;
        this.message = message;
    }

    // Getters et Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getChargeId() {
        return chargeId;
    }
    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
