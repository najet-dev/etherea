package com.etherea.dtos;

import com.etherea.models.Volume;

public class VolumeDTO {
    private Long id;
    private String volume;  // Changement de String à int
    private double price; // Ajout de la propriété price
    public VolumeDTO() {
    }
    public VolumeDTO(Long id, String volume, double price) {
        this.id = id;
        this.volume = volume;
        this.price = price;
    }
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public static VolumeDTO fromVolume(Volume volume) {
        return new VolumeDTO(volume.getId(), volume.getVolume(), volume.getPrice());
    }
    public Volume toVolume() {
        Volume volume = new Volume();
        volume.setId(this.id);
        volume.setVolume(this.volume);
        volume.setPrice(this.price);
        return volume;
    }
}
