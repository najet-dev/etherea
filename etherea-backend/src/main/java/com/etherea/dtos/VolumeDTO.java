package com.etherea.dtos;

import com.etherea.models.Product;
import com.etherea.models.Volume;

import java.math.BigDecimal;

public class VolumeDTO {
    private Long id;
    private Long productId;
    private int volume;
    private BigDecimal price;
    public VolumeDTO() {}
    public VolumeDTO(Long id, Long productId, int volume, BigDecimal price) {
        this.id = id;
        this.productId = productId;
        this.volume = volume;
        this.price = price;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public int getVolume() {
        return volume;
    }
    public void setVolume(int volume) {
        this.volume = volume;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // Convert a Volume to VolumeDTO
    public static VolumeDTO fromVolume(Volume volume) {
        if (volume == null) {
            return null;
        }
        return new VolumeDTO(
                volume.getId(),
                volume.getProduct().getId(),
                volume.getVolume(),
                volume.getPrice()
        );
    }
    public Volume toVolume(Product product) {
        Volume volume = new Volume();
        volume.setId(this.id);
        volume.setProduct(product);
        volume.setVolume(this.volume);
        volume.setPrice(this.price);
        return volume;
    }
}
