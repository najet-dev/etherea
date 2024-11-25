package com.etherea.models;

import com.etherea.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne(optional = true)
    @JoinColumn(name = "volume_id", nullable = true)
    private Volume volume;
    @ManyToOne
    @JoinColumn(name = "cartId")
    @JsonIgnore
    private Cart cart;
    private BigDecimal subTotal;
    private BigDecimal total;
    public CartItem() {
    }
    public CartItem(Long id, int quantity, Product product, Volume volume, Cart cart) {
        this.id = id;
        this.quantity = quantity;
        this.product = product;
        this.volume = volume;
        this.cart = cart;
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
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Volume getVolume() {
        return volume;
    }
    public void setVolume(Volume volume) {
        this.volume = volume;
    }
    public Cart getCart() {
        return cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    public BigDecimal getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    // Method for calculating the subtotal
    public BigDecimal calculateSubtotal() {
        if (product.getType() == ProductType.FACE) {
            // Use basePrice for FACE products
            return product.getBasePrice().multiply(BigDecimal.valueOf(quantity));
        } else if (volume != null && volume.getPrice() != null) {
            // Use volume pricing for HAIR products
            return volume.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO; // // Returns 0 if no condition is met
    }
    // Method for calculating the total price of all products in the cart shopping
    public static BigDecimal calculateTotalPrice(List<CartItem> items) {
        return items.stream()
                .map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}