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
    private VolumeDTO volume;
    private Long userId;
    public CartItemDTO() {}
    public CartItemDTO(Long id, int quantity, Long productId, VolumeDTO volume, Long userId) {
        this.id = id;
        this.quantity = quantity;
        this.productId = productId;
        this.volume = volume;
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

    // Convert CartItem to CartItemDTO
    public static CartItemDTO fromCartItem(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        // Convert volume to VolumeDTO
        VolumeDTO volumeDTO = null;
        if (cartItem.getVolume() != null) {
            volumeDTO = VolumeDTO.fromVolume(cartItem.getVolume());
        }
        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getQuantity(),
                cartItem.getProduct() != null ? cartItem.getProduct().getId() : null,
                volumeDTO,
                cartItem.getUser() != null ? cartItem.getUser().getId() : null
        );
    }

    // Convert CartItemDTO to CartItem
    public CartItem toCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(this.id);
        cartItem.setQuantity(this.quantity);

        Product product = new Product();
        product.setId(this.productId);
        cartItem.setProduct(product);

        // Convert volumeDTO to Volume
        Volume volume = null;
        if (this.volume != null) {
            volume = this.volume.toVolume();  // Reverse conversion
            cartItem.setVolume(volume);
        }

        User user = new User();
        user.setId(this.userId);
        cartItem.setUser(user);

        return cartItem;
    }
}
