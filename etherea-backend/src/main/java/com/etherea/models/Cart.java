package com.etherea.models;

import com.etherea.enums.CartStatus;
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

    @Enumerated(EnumType.STRING)
    private CartStatus status;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_type_id")
    private DeliveryType deliveryType;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethod deliveryMethod;
    public Cart() {
        this.status = CartStatus.ACTIVE;
    }
    public Cart(User user, DeliveryType deliveryType, DeliveryMethod deliveryMethod) {
        this();
        this.user = user;
        this.deliveryType = deliveryType;
        this.deliveryMethod = deliveryMethod;
    }
    public Cart(User user) {
        this();
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CartStatus getStatus() { return status; }
    public void setStatus(CartStatus status) { this.status = status; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
    }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public DeliveryType getDeliveryType() { return deliveryType; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }
    public DeliveryMethod getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(DeliveryMethod deliveryMethod) { this.deliveryMethod = deliveryMethod; }

    // Calcul du total des articles
    public BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    // Vérifie si la livraison est gratuite
    public boolean isFreeShipping() {
        return FreeShippingChecker.isFreeShipping(calculateTotalAmount());
    }

    // Calcul du coût de la livraison
    public BigDecimal calculateDeliveryCost() {
        return (deliveryType == null || isFreeShipping()) ? BigDecimal.ZERO : deliveryType.getCost();
    }

    // Calcul du total final avec livraison
    public BigDecimal calculateFinalTotal() {
        return calculateTotalAmount().add(calculateDeliveryCost());
    }

}
