package com.etherea.dtos;

import com.etherea.models.CartItem;

public class CartItemDTO {
    private Long id;
    private int quantity;
    private double subTotal;
    private double total;
    private Long productId;
    private Long userId;
    public CartItemDTO() {
    }
    public CartItemDTO(Long id, int quantity, double subTotal, double total, Long productId, Long userId) {
        this.id = id;
        this.quantity = quantity;
        this.subTotal = subTotal;
        this.total = total;
        this.productId = productId;
        this.userId = userId;
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
    public static CartItemDTO fromCartItem(CartItem cartItem) {
        ProductDTO productDTO = ProductDTO.fromProduct(cartItem.getProduct());
        UserDTO userDTO = UserDTO.fromUser(cartItem.getUser());
        return new CartItemDTO(cartItem.getId(), cartItem.getQuantity(), cartItem.getSubTotal(), cartItem.getTotal(), productDTO.getId(), userDTO.getId());
    }
}
