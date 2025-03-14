package com.etherea.dtos;

import com.etherea.models.Volume;

import java.math.BigDecimal;

public class VolumeDTO {
    private Long id;
    private int volume;
    private BigDecimal price;
    public VolumeDTO() {}
    public VolumeDTO(Long id, int volume, BigDecimal price) {
        this.id = id;
        this.volume = volume;
        this.price = price;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    // Method for converting a Volume to a VolumeDTO
    public static VolumeDTO fromVolume(Volume volume) {
        if (volume == null) {
            return null;
        }
        return new VolumeDTO(
                volume.getId(),
                volume.getVolume(),
                volume.getPrice()
        );
    }
    // Method for converting a VolumeDTO to a Volume
    public Volume toVolume() {
        Volume volume = new Volume();
        volume.setId(this.id);
        volume.setVolume(this.volume);
        volume.setPrice(this.price);
        return volume;
    }
}