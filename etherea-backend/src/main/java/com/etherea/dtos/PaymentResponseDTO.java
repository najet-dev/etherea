package com.etherea.dtos;

import com.etherea.enums.PaymentStatus;

public class PaymentResponseDTO {
    private String clientSecret;

    public PaymentResponseDTO(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}

