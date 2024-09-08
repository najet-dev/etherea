package com.etherea.dtos;

import com.etherea.models.CartItem;

public class CartItemDTO {
    private Long id;
    private int quantity;
    private Long productId;
    private Long volumeId;  // Ajout du champ volumeId
    private Long userId;

    // Constructeurs
    public CartItemDTO() {
    }
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
    // Méthode de conversion de CartItem à CartItemDTO
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
}
