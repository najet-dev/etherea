package com.etherea.dtos;

import com.etherea.enums.PaymentOption;
import com.etherea.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {
    @NotNull
    private PaymentOption paymentOption;
    @NotNull
    private PaymentStatus paymentStatus;
    @NotNull
    private Long cartId;

    public PaymentOption getPaymentOption() {
        return paymentOption;
    }
    public void setPaymentOption(PaymentOption paymentOption) {
        this.paymentOption = paymentOption;
    }
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public Long getCartId() {
        return cartId;
    }
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}
