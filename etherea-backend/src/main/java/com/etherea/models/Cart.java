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

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethod deliveryMethod;

    private BigDecimal finalTotal; // Champ pour le total global (articles + livraison)

    public Cart() {}

    public Cart(Long id, List<CartItem> items, User user, DeliveryMethod deliveryMethod) {
        this.id = id;
        this.items = items;
        this.user = user;
        this.deliveryMethod = deliveryMethod;
        this.finalTotal = calculateFinalTotal(); // Initialisation du finalTotal
    }

    // Getters et setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public List<CartItem> getItems() {
        return items;
    }
    public void setItems(List<CartItem> items) {
        this.items = items;
        updateFinalTotal(); // Recalculer et mettre à jour finalTotal chaque fois que la liste des articles change
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
        updateFinalTotal(); // Recalculer finalTotal lors du changement de méthode de livraison
    }
    public BigDecimal getFinalTotal() {
        return finalTotal;
    }
    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
    }

    // Méthode pour calculer le total des articles
    public BigDecimal calculateTotalAmount() {
        if (items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Vérifier si la livraison est gratuite
    public boolean isFreeShipping() {
        if (deliveryMethod == null) {
            throw new IllegalStateException("Delivery method must be specified.");
        }
        return deliveryMethod.isFreeShipping(calculateTotalAmount().doubleValue());
    }

    // Calcul du coût de livraison
    public double calculateDeliveryCost() {
        if (deliveryMethod == null) {
            throw new IllegalStateException("Delivery method must be specified.");
        }
        return deliveryMethod.calculateCost(calculateTotalAmount().doubleValue());
    }

    // Calculer le total final
    public BigDecimal calculateFinalTotal() {
        return calculateTotalAmount().add(BigDecimal.valueOf(calculateDeliveryCost()));
    }

    // Mettre à jour finalTotal chaque fois que nécessaire
    private void updateFinalTotal() {
        this.finalTotal = calculateFinalTotal();
    }
}
