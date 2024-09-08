package com.etherea.dtos;

import com.etherea.models.Volume;

import java.math.BigDecimal;
public class VolumeDTO {
    private Long id;
    private Integer volume;
    private BigDecimal price;
    public VolumeDTO() {
    }
    public VolumeDTO(Long id, Integer volume, BigDecimal  price) {
        this.id = id;
        this.volume = volume;
        this.price = price;
    }
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }
    public BigDecimal  getPrice() {
        return price;
    }
    public void setPrice(BigDecimal  price) {
        this.price = price;
    }
    // Method to convert from Volume entity to VolumeDTO
    public static VolumeDTO fromVolume(Volume volume) {
        VolumeDTO dto = new VolumeDTO();
        dto.setId(volume.getId());
        dto.setVolume(volume.getVolume());
        dto.setPrice(volume.getPrice());
        return dto;
    }
    // Method to convert from VolumeDTO to Volume entity
    public Volume toVolume() {
        Volume volume = new Volume();
        volume.setId(this.id);
        volume.setVolume(this.volume);
        volume.setPrice(this.price);
        return volume;
    }
}
