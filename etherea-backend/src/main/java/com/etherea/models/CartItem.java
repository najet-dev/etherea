package com.etherea.models;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private double subTotal;
    private double total;
    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore // Ignorer la sérialisation de cette propriété pour éviter la récursion infinie
    private User user;
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "cartId")
    private Cart cart;

    public CartItem() {
    }
    public CartItem(Long id,int quantity, double subTotal, double total, Product product, Cart cart) {
        this.id = id;
        this.quantity = quantity;
        this.subTotal = subTotal;
        this.total = total;
        this.product = product;
        this.cart = cart;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        // Recalculer le sous-total et le total après la mise à jour de la quantité
        this.subTotal = calculateSubtotal();
        this.total = calculateTotal();
    }
    public double getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
    public double getTotal() {
        return total;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public int getQuantity() {
        return this.quantity;
    }
    public Cart getCart() {
        return cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    // Méthode pour calculer le sous-total (prix * quantité) d'un produit
    public double calculateSubtotal() {
        double subtotal = getProduct().getPrice() * getQuantity();
        return subtotal;
    }
    // Méthode pour calculer le total (prix total pour tous les produits dans le panier)
    public double calculateTotal() {
        double total = calculateSubtotal(); // appel à la méthode calculateSubtotal pour obtenir le sous-total
        return total;
    }
    // Méthode pour calculer le prix total de tous les produits dans le panier
    public static double calculateTotalPrice(List<CartItem> items) {
        double totalPrice = 0.0;

        for (CartItem cartItem : items) {
            totalPrice += cartItem.calculateSubtotal();
        }
        return totalPrice;
    }

}
