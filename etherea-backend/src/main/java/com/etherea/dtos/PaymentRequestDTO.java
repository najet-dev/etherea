package com.etherea.dtos;

import com.etherea.enums.PaymentOption;
import com.etherea.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {
    @NotNull
    private Long cartId;
    public Long getCartId() {
        return cartId;
    }
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}
