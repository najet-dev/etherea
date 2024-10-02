package com.etherea.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Volume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int volume;
    private Integer volume;
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Constructeurs
    public Volume() {
    }
    public Volume(int volume, BigDecimal price, Product product) {
    public Volume() {
    }
    public Volume(Integer volume, BigDecimal price, Product product) {
        this.volume = volume;
        this.price = price;
        this.product = product;
    }
    // Getters and Setters
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

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}