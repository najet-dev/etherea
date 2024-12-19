package com.etherea.dtos;

public class CartWithDeliveryDTO {
    private double cartTotal;
    private double deliveryCost;
    private double total;

    public CartWithDeliveryDTO(double cartTotal, double deliveryCost, double total) {
        this.cartTotal = cartTotal;
        this.deliveryCost = deliveryCost;
        this.total = total;
    }
    public double getCartTotal() {
        return cartTotal;
    }
    public void setCartTotal(double cartTotal) {
        this.cartTotal = cartTotal;
    }
    public double getDeliveryCost() {
        return deliveryCost;
    }
    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
}
