package com.etherea.dtos;

import com.etherea.models.CartItem;
import com.etherea.models.Product;
import com.etherea.models.ProductVolume;

public class CartItemDTO {
    private Long id;
    private int quantity;
    private double subTotal;
    private double total;
    private Long productId;
    private Long userId;
    private Long productVolumeId;
    public CartItemDTO() {}
    public CartItemDTO(Long id, int quantity, double subTotal, double total, Long productId, Long userId, Long productVolumeId) {
        this.id = id;
        this.quantity = quantity;
        this.subTotal = subTotal;
        this.total = total;
        this.productId = productId;
        this.userId = userId;
        this.productVolumeId = productVolumeId;
    }
    // Getters and setters
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
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getProductVolumeId() {
        return productVolumeId;
    }
    public void setProductVolumeId(Long productVolumeId) {
        this.productVolumeId = productVolumeId;
    }
    public static CartItemDTO fromCartItem(CartItem cartItem) {
        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getQuantity(),
                cartItem.getSubTotal(),
                cartItem.getTotal(),
                cartItem.getProduct() != null ? cartItem.getProduct().getId() : null,
                cartItem.getUser() != null ? cartItem.getUser().getId() : null,
                cartItem.getProductVolume() != null ? cartItem.getProductVolume().getId() : null
        );
    }
    public CartItem toCartItem(Product product, ProductVolume productVolume) {
        CartItem cartItem = new CartItem();
        cartItem.setId(this.id);
        cartItem.setQuantity(this.quantity);
        cartItem.setProduct(product);
        cartItem.setProductVolume(productVolume);
        cartItem.setSubTotal(this.subTotal);
        cartItem.setTotal(this.total);
        return cartItem;
    }
}
