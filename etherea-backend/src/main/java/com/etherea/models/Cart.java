package com.etherea.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethod deliveryMethod;
    public Cart() {
    }
    public Cart(User user) {
        this.user = user;
    }
    // Getters et Setters
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
    }
    public BigDecimal calculateTotalAmount() {
        BigDecimal total = items.stream()
                .map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total du panier : " + total); // Log pour debug
        return total;
    }

    public double calculateDeliveryCost() {
        if (deliveryMethod == null) {
            System.out.println("Aucune méthode de livraison sélectionnée."); // Log pour debug
            return 0.0;
        }

        double totalAmount = calculateTotalAmount().doubleValue();
        double deliveryCost = deliveryMethod.isFreeShipping(totalAmount)
                ? 0.0
                : deliveryMethod.calculateCost(totalAmount);

        System.out.println("Type de livraison : " + deliveryMethod.getClass().getSimpleName());
        System.out.println("Montant total du panier : " + totalAmount);
        System.out.println(deliveryCost == 0.0 ? "Livraison gratuite appliquée." : "Coût de livraison : " + deliveryCost);

        return deliveryCost;
    }

}