package com.etherea.dtos;

import com.etherea.enums.CartStatus;
import com.etherea.models.Cart;
import com.etherea.models.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CartDTO {
    private Long id;
    private UserDTO user;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;
    private BigDecimal deliveryCost;
    private BigDecimal finalTotal;
    private boolean freeShipping;
    private CartStatus status;
    private DeliveryMethodDTO deliveryMethod;
    public CartDTO() {}

    public CartDTO(Long id, UserDTO user, List<CartItemDTO> items, BigDecimal totalAmount, BigDecimal deliveryCost,
                   BigDecimal finalTotal, boolean freeShipping, CartStatus status, DeliveryMethodDTO deliveryMethod) {
        this.id = id;
        this.user = user;
        this.items = items;
        this.totalAmount = totalAmount;
        this.deliveryCost = deliveryCost;
        this.finalTotal = finalTotal;
        this.freeShipping = freeShipping;
        this.status = status;
        this.deliveryMethod = deliveryMethod;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
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
    public CartStatus getStatus() { return status; }
    public void setStatus(CartStatus status) { this.status = status; }
    public DeliveryMethodDTO getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(DeliveryMethodDTO deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public static CartDTO fromCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Le panier ne peut pas être null.");
        }

        return new CartDTO(
                cart.getId(),
                cart.getUser() != null ? UserDTO.fromUser(cart.getUser()) : null,
                cart.getItems() != null ? cart.getItems().stream()
                        .map(CartItemDTO::fromCartItem)
                        .collect(Collectors.toList()) : List.of(),
                cart.calculateTotalAmount(),
                cart.calculateDeliveryCost(),
                cart.calculateFinalTotal(),
                cart.isFreeShipping(),
                cart.getStatus(),
                cart.getDeliveryMethod() != null ? DeliveryMethodDTO.fromEntity(cart.getDeliveryMethod()) : null
        );
    }
    public Cart toCart() {
        Cart cart = new Cart();
        cart.setId(this.id);

        if (this.user != null) {
            User user = new User();
            user.setId(this.user.getId());
            cart.setUser(user);
        }

        cart.setStatus(this.status);

        return cart;
    }
}
