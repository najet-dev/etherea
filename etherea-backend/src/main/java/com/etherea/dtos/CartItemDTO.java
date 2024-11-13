package com.etherea.dtos;

import com.etherea.models.CartItem;
import com.etherea.models.Product;
import com.etherea.models.User;
import com.etherea.models.Volume;

import java.math.BigDecimal;

public class CartItemDTO {

    private Long id;
    private int quantity;
    private Long productId;
    private VolumeDTO volume;
    private Long userId;
    private BigDecimal subTotal;

    public CartItemDTO() {}

    public CartItemDTO(Long id, int quantity, Long productId, VolumeDTO volume, Long userId, BigDecimal subTotal) {
        this.id = id;
        this.quantity = quantity;
        this.productId = productId;
        this.volume = volume;
        this.userId = userId;
        this.subTotal = subTotal;
    }

    // Getters et Setters
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
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public BigDecimal getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
    public static CartItemDTO fromCartItem(CartItem cartItem) {
        if (cartItem == null) return null;

        BigDecimal subTotal = cartItem.calculateSubtotal();
        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getQuantity(),
                cartItem.getProduct() != null ? cartItem.getProduct().getId() : null,
                VolumeDTO.fromVolume(cartItem.getVolume()),
                cartItem.getUser() != null ? cartItem.getUser().getId() : null,
                subTotal
        );
    }
    public CartItem toCartItem(User user, Product product, Volume volume) {
        CartItem cartItem = new CartItem();
        cartItem.setId(this.id);  // Optionnel pour mise à jour
        cartItem.setQuantity(this.quantity);
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setVolume(volume);
        cartItem.setSubTotal(cartItem.calculateSubtotal());
        return cartItem;
    }
}
