package com.etherea.dtos;

import com.etherea.dtos.CartItemDTO;
import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.models.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CartDTO {
    private Long cartId;
    private List<CartItemDTO> items;
    private double totalAmount;
    private double deliveryCost;
    private DeliveryMethodDTO deliveryMethod;
    public CartDTO() {}
    public CartDTO(Long cartId, List<CartItemDTO> items, double totalAmount, double deliveryCost, DeliveryMethodDTO deliveryMethod) {
        this.cartId = cartId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.deliveryCost = deliveryCost;
        this.deliveryMethod = deliveryMethod;
    }
    public Long getCartId() {
        return cartId;
    }
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
    public List<CartItemDTO> getItems() {
        return items;
    }
    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public double getDeliveryCost() {
        return deliveryCost;
    }
    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }
    public DeliveryMethodDTO getDeliveryMethod() {
        return deliveryMethod;
    }
    public void setDeliveryMethod(DeliveryMethodDTO deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    // Méthode pour convertir Cart en CartDTO
    public static CartDTO fromCart(Cart cart, DeliveryMethodDTO deliveryMethodDTO) {
        BigDecimal totalAmount = cart.calculateTotalAmount();
        double deliveryCost = cart.calculateDeliveryCost();

        return new CartDTO(
                cart.getId(),
                cart.getItems().stream().map(CartItemDTO::fromCartItem).collect(Collectors.toList()),
                totalAmount.doubleValue(),  // Convertit en double après les calculs
                deliveryCost,
                deliveryMethodDTO
        );
    }

}