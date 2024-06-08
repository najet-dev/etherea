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
    @JoinColumn(name = "productVolumeId", nullable = false)
    private ProductVolume productVolume;
    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore // Ignore serialization to avoid infinite recursion
    private User user;
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "cartId", nullable = false)
    private Cart cart;
    public CartItem() {}
    public CartItem(int quantity, ProductVolume productVolume, Product product, Cart cart) {
        this.quantity = quantity;
        this.productVolume = productVolume;
        this.product = product;
        this.cart = cart;
        this.subTotal = calculateSubtotal();
        this.total = calculateTotal();
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        // Recalculate subtotal and total after updating quantity
        this.subTotal = calculateSubtotal();
        this.total = calculateTotal();
    }
    public ProductVolume getProductVolume() {
        return productVolume;
    }
    public void setProductVolume(ProductVolume productVolume) {
        this.productVolume = productVolume;
        // Recalculate subtotal and total after updating product volume
        this.subTotal = calculateSubtotal();
        this.total = calculateTotal();
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
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
    public void setTotal(double total) {
        this.total = total;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Cart getCart() {
        return cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    // Method to calculate subtotal (price * quantity) of a product
    public double calculateSubtotal() {
        return getProductVolume().getPrice() * getQuantity();
    }
    // Method to calculate total (currently identical to subtotal)
    public double calculateTotal() {
        return calculateSubtotal();
    }
    // Method to calculate the total price of all products in the cart
    public static double calculateTotalPrice(List<CartItem> items) {
        return items.stream()
                .mapToDouble(CartItem::calculateSubtotal)
                .sum();
    }
}
