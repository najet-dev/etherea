package com.etherea.dtos;

import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {
    @NotNull
    private String cardNumber;
    @NotNull
    private String expiryDate;
    @NotNull
    private String cvc;
    @NotNull
    private Long cartId;

    // Getters et Setters
    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    public String getCvc() {
        return cvc;
    }
    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
    public Long getCartId() {
        return cartId;
    }
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}
