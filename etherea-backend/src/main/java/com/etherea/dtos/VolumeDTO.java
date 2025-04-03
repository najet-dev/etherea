package com.etherea.dtos;

import com.etherea.models.Product;
import com.etherea.models.Volume;

import java.math.BigDecimal;

public class VolumeDTO {
    private Long id;
    private String productName;
    private int volume;
    private BigDecimal price;
    public VolumeDTO() {}
    public VolumeDTO(Long id, String productName, int volume, BigDecimal price) {
        this.id = id;
        this.productName = productName;
        this.volume = volume;
        this.price = price;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
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
                volume.getProduct().getName(),
                volume.getVolume(),
                volume.getPrice()
        );
    }
    public Volume toVolume(Product product) {
        Volume volume = new Volume();
        volume.setProduct(product);
        volume.setId(this.id);
        volume.setPrice(this.price);
        volume.setVolume(this.volume);
        return volume;
    }
}