package com.etherea.dtos;

import java.math.BigDecimal;

public class CartWithDeliveryDTO {
    private BigDecimal cartTotal;
    private BigDecimal deliveryCost;
    private BigDecimal total;
    public CartWithDeliveryDTO(BigDecimal cartTotal, BigDecimal deliveryCost, BigDecimal total) {
        this.cartTotal = cartTotal;
        this.deliveryCost = deliveryCost;
        this.total = total;
    }
    public BigDecimal getCartTotal() {
        return cartTotal;
    }
    public void setCartTotal(BigDecimal cartTotal) {
        this.cartTotal = cartTotal;
    }
    public BigDecimal getDeliveryCost() {
        return deliveryCost;
    }
    public void setDeliveryCost(BigDecimal deliveryCost) {
        this.deliveryCost = deliveryCost;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
