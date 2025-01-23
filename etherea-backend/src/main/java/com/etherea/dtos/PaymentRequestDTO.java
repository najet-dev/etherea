package com.etherea.dtos;

import com.etherea.enums.PaymentOption;
import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {
    @NotNull
    private String cardNumber;
    @NotNull
    private String expiryDate;
    @NotNull
    private String cvc;
    @NotNull
    private PaymentOption paymentOption;
    @NotNull
    private Long cartId;
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
    public PaymentOption getPaymentOption() {
        return paymentOption;
    }
    public void setPaymentOption(PaymentOption paymentOption) {
        this.paymentOption = paymentOption;
    }
    public Long getCartId() {
        return cartId;
    }
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}
