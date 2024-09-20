package com.etherea.dtos;

import com.etherea.models.CartItem;

import java.math.BigDecimal;

public class CartItemDTO {
    private Long id;
    private int quantity;
    private Long productId;
    private VolumeDTO volume;    // Contient les détails du volume
    private Long userId;
    private BigDecimal subTotal; // Sous-total calculé

    // Constructeurs
    public CartItemDTO() {
    }

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

    // Méthode de conversion de CartItem à CartItemDTO
    public static CartItemDTO fromCartItem(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        BigDecimal subTotal;
        Long volumeId = null;

        if (cartItem.getVolume() != null) {
            volumeId = cartItem.getVolume().getId();
            subTotal = cartItem.getVolume().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        } else if (cartItem.getProduct() != null && "FACE".equals(cartItem.getProduct().getType())) {
            // Pour les produits de type FACE, utiliser un prix de base
            subTotal = cartItem.getProduct().getBasePrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        } else {
            // Si aucun volume n'est disponible et ce n'est pas un produit de type FACE
            subTotal = BigDecimal.ZERO; // Ajuste selon ta logique
        }

        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getQuantity(),
                cartItem.getProduct() != null ? cartItem.getProduct().getId() : null,
                VolumeDTO.fromVolume(cartItem.getVolume()), // Assure-toi que cette méthode gère null
                cartItem.getUser() != null ? cartItem.getUser().getId() : null,
                subTotal
        );
    }

}
