package com.etherea.dtos;

import com.etherea.models.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CartDTO {
    private Long cartId;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;
    private BigDecimal deliveryCost;
    private BigDecimal finalTotal;
    private boolean freeShipping;
    private DeliveryMethodDTO deliveryMethod;
    public CartDTO() {}
    public CartDTO(Long cartId, List<CartItemDTO> items, BigDecimal totalAmount, BigDecimal deliveryCost,
                   BigDecimal finalTotal, boolean freeShipping, DeliveryMethodDTO deliveryMethod) {
        this.cartId = cartId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.deliveryCost = deliveryCost;
        this.finalTotal = finalTotal;
        this.freeShipping = freeShipping;
        this.deliveryMethod = deliveryMethod;
    }
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getDeliveryCost() { return deliveryCost; }
    public void setDeliveryCost(BigDecimal deliveryCost) { this.deliveryCost = deliveryCost; }
    public BigDecimal getFinalTotal() { return finalTotal; }
    public void setFinalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; }
    public boolean isFreeShipping() { return freeShipping; }
    public void setFreeShipping(boolean freeShipping) { this.freeShipping = freeShipping; }
    public DeliveryMethodDTO getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(DeliveryMethodDTO deliveryMethod) { this.deliveryMethod = deliveryMethod; }

    // Convertit un `Cart` en `CartDTO`
    public static CartDTO fromCart(Cart cart, DeliveryMethodDTO deliveryMethodDTO) {
        if (cart == null) {
            throw new IllegalArgumentException("Le panier doit être fourni.");
        }
        return new CartDTO(
                cart.getId(),
                cart.getItems() != null ? cart.getItems().stream()
                        .map(CartItemDTO::fromCartItem)
                        .collect(Collectors.toList()) : List.of(),
                cart.calculateTotalAmount(),
                cart.calculateDeliveryCost(),
                cart.calculateFinalTotal(),
                cart.isFreeShipping(),
                deliveryMethodDTO
        );
    }
}
