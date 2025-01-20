package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean isUsed = false;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethod deliveryMethod;
    private BigDecimal finalTotal = BigDecimal.ZERO;
    public Cart() {}
    public Cart(Long id, boolean isUsed, List<CartItem> items, User user, DeliveryMethod deliveryMethod) {
        this.id = id;
        this.isUsed = isUsed;
        this.items = items;
        this.user = user;
        this.deliveryMethod = deliveryMethod;
        this.finalTotal = calculateFinalTotal();
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public boolean isUsed() {
        return isUsed;
    }
    public void setUsed(boolean used) {
        isUsed = used;
    }
    public List<CartItem> getItems() {
        return items;
    }
    public void setItems(List<CartItem> items) {
        this.items = items;
        updateFinalTotal(); // Recalculate and update finalTotal each time the item list changes
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }
    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
        updateFinalTotal();
    }
    public BigDecimal getFinalTotal() {
        return finalTotal;
    }
    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
    }

    // Method for calculating item totals
    public BigDecimal calculateTotalAmount() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    // Check if delivery is free
    public boolean isFreeShipping() {
        if (deliveryMethod == null) {
            throw new IllegalStateException("Delivery method must be specified.");
        }
        return deliveryMethod.isFreeShipping(calculateTotalAmount().doubleValue());
    }

    // Delivery cost calculation
    public double calculateDeliveryCost() {
        if (deliveryMethod == null) {
            return 0.0;
        }
        return deliveryMethod.calculateCost(calculateTotalAmount().doubleValue());
    }
    public BigDecimal calculateFinalTotal() {
        return calculateTotalAmount().add(BigDecimal.valueOf(calculateDeliveryCost()));
    }
    private void updateFinalTotal() {
        this.finalTotal = calculateFinalTotal();
    }

}
