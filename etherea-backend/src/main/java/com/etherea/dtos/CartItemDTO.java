package com.etherea.dtos;

import com.etherea.models.CartItem;
import com.etherea.models.Product;
import com.etherea.models.Volume;
import java.math.BigDecimal;
public class CartItemDTO {
    private Long id;
    private int quantity;
    private Long productId;
    private VolumeDTO volume;
    private BigDecimal subTotal;
    private Long userId;
    private CartDTO cart;
    public CartItemDTO() {}
    public CartItemDTO(Long id, int quantity, Long productId, VolumeDTO volume, BigDecimal subTotal, Long userId, CartDTO cart) {
        this.id = id;
        this.quantity = quantity;
        this.productId = productId;
        this.volume = volume;
        this.subTotal = subTotal;
        this.userId = userId;
        this.cart = cart;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public CartDTO getCart() {
        return cart;
    }
    public void setCart(CartDTO cart) {
        this.cart = cart;
        if (cart != null && cart.getUser() != null) {
            this.userId = cart.getUser().getId();
        }
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
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public VolumeDTO getVolume() {
        return volume;
    }
    public void setVolume(VolumeDTO volume) {
        this.volume = volume;
    }
    public BigDecimal getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
    public static CartItemDTO fromCartItem(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getQuantity(),
                cartItem.getProduct() != null ? cartItem.getProduct().getId() : null,
                cartItem.getVolume() != null ? VolumeDTO.fromVolume(cartItem.getVolume()) : null,
                cartItem.calculateSubtotal(),
                cartItem.getCart() != null && cartItem.getCart().getUser() != null ? cartItem.getCart().getUser().getId() : null,
                null  // Do not convert `Cart` to avoid the infinite loop
        );
    }
    public CartItem toCartItem(Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setId(this.id);
        cartItem.setQuantity(this.quantity);
        cartItem.setCart(null);  // Assuming you don't need to set cart here

        // Set the product object by passing the product parameter
        if (this.productId != null && product != null) {
            product.setId(this.productId);
            cartItem.setProduct(product);
        }

        // Ensure that the volume is set correctly
        if (this.volume != null) {
            cartItem.setVolume(this.volume.toVolume(product));
        }

        return cartItem;
    }
}