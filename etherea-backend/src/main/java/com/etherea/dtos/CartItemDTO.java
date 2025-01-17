package com.etherea.dtos;

import com.etherea.models.CartItem;
import com.etherea.models.Product;
import com.etherea.models.Volume;
import com.etherea.models.User;

import java.math.BigDecimal;

public class CartItemDTO {
    private Long id;
    private int quantity;
    private Long productId;
    private Long volumeId;
    private Long userId;
    private BigDecimal subTotal; 

    // Constructeurs
    public CartItemDTO() {}

    public CartItemDTO(Long id, int quantity, Long productId, Long volumeId, Long userId) {
        this.id = id;
        this.quantity = quantity;
        this.productId = productId;
        this.volumeId = volumeId;
        this.userId = userId;
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
    public Long getVolumeId() {
        return volumeId;
    }
    public void setVolumeId(Long volumeId) {
        this.volumeId = volumeId;
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

    // Conversion de CartItem vers CartItemDTO
    public static CartItemDTO fromCartItem(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getQuantity(),
                cartItem.getProduct() != null ? cartItem.getProduct().getId() : null,
                cartItem.getVolume() != null ? cartItem.getVolume().getId() : null,
                cartItem.getUser() != null ? cartItem.getUser().getId() : null
        );
    }

    // Conversion de CartItemDTO vers CartItem
    public CartItem toCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(this.id);
        cartItem.setQuantity(this.quantity);

        Product product = new Product();
        product.setId(this.productId);
        cartItem.setProduct(product);

        Volume volume = null;
        if (this.volumeId != null) {
            volume = new Volume();
            volume.setId(this.volumeId);
            cartItem.setVolume(volume);
        }

        // Assigner l'utilisateur
        User user = new User();
        user.setId(this.userId);
        cartItem.setUser(user);

        return cartItem;
    }
}
