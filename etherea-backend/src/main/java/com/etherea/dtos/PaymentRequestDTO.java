package com.etherea.dtos;

import com.etherea.enums.PaymentOption;
import com.etherea.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {
    @NotNull
    private PaymentOption paymentOption;
    @NotNull
    private Long cartId;
    public PaymentRequestDTO() {
    }
    public PaymentRequestDTO(PaymentOption paymentOption, Long cartId) {
        this.paymentOption = paymentOption;
        this.cartId = cartId;
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