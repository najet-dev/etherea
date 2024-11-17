package com.etherea.models;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethod deliveryMethod;
    public Cart() {}
    public Cart(User user) {
        this.user = user;
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
    // Method for calculating the total number of products in the cart shopping
    public BigDecimal calculateTotalAmount() {
        // Calls CartItem's static method to calculate the total
        BigDecimal total = CartItem.calculateTotalPrice(items);
        System.out.println("Total calculated cart shopping: " + total);
        return total;
    }

    // Method for calculating delivery costs
    public double calculateDeliveryCost() {
        if (deliveryMethod == null) {
            throw new IllegalStateException("No delivery method is selected.");
        }

        double totalAmount = calculateTotalAmount().doubleValue();

        // Vérification si la livraison est gratuite
        if (deliveryMethod.isFreeShipping(totalAmount)) {
            System.out.println("Free delivery applied.");
            return 0.0;
        }

        // Sinon, calcul du coût selon la méthode de livraison
        double deliveryCost = deliveryMethod.calculateCost(totalAmount);
        System.out.println("Calculated delivery cost : " + deliveryCost);
        return deliveryCost;
    }
}
