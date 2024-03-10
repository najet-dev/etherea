package com.etherea.models;



import jakarta.persistence.*;

import java.util.List;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int subTotal;
    private int total;
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "cartId")
    private Cart cart;

    public CartItem() {
    }
    public CartItem(Long id, int subTotal, int total, Product product, Cart cart) {
        this.id = id;
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

    public int getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public int getQuantity() {
        return getProduct().getQuantity();
    }
    public Cart getCart() {
        return cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    // Méthode pour calculer le sous-total (prix * quantité) d'un produit
    public double calculateSubtotal() {
        return getProduct().getPrice() * getQuantity();
    }

    // Méthode pour calculer le total (prix total pour ce produit dans le panier)
    public double calculateTotal() {
        return calculateSubtotal();
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
