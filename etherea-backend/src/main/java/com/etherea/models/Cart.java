package com.etherea.models;

import com.etherea.utils.FreeShippingChecker;
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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_type_id")
    private DeliveryType deliveryType;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethod deliveryMethod;
    public Cart() {}
    public Cart(Long id, boolean isUsed, List<CartItem> items, User user, DeliveryType deliveryType,  DeliveryMethod deliveryMethod) {
        this.id = id;
        this.isUsed = isUsed;
        this.items = items != null ? items : new ArrayList<>();
        this.user = user;
        this.deliveryType = deliveryType;
        this.deliveryMethod = deliveryMethod;
    }
    public Long getId() { return id; }
    public void setId(Long id) {
        this.id = id;
    }
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items != null ? items : new ArrayList<>(); }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public DeliveryType getDeliveryType() { return deliveryType; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }
    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }
    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public boolean isFreeShipping() {
        return FreeShippingChecker.isFreeShipping(calculateTotalAmount());
    }
    public BigDecimal calculateDeliveryCost() {
        return (deliveryType == null || isFreeShipping()) ? BigDecimal.ZERO : deliveryType.getCost();
    }
    public BigDecimal calculateFinalTotal() {
        return calculateTotalAmount().add(calculateDeliveryCost());
    }
}
