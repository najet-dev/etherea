package com.etherea.dtos;

import com.etherea.enums.PaymentOption;

public class PaymentRequestDTO {
    private PaymentOption paymentOption;
    private String currency;
    private Long amount;
    private String paymentMethodId;
    public PaymentRequestDTO() {}
    public PaymentRequestDTO(PaymentOption paymentOption, String currency, Long amount) {
        this.paymentOption = paymentOption;
        this.currency = currency;
        this.amount = amount;
    }

    // Getters et Setters
    public PaymentOption getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(PaymentOption paymentOption) {
        this.paymentOption = paymentOption;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
