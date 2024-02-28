package com.etherea.models;

import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stockAvailable;
    private String image;
    @OneToMany(mappedBy = "product")
    private List<CommandItem> commandItems = new ArrayList<>();
    public Product() {
    }
    public Product(String name, String description, double price, int stockAvailable, String image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockAvailable = stockAvailable;
        this.image = image;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getStockAvailable() {
        return stockAvailable;
    }
    public void setStockAvailable(int stockAvailable) {
        this.stockAvailable = stockAvailable;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public List<CommandItem> getCommandItems() {
        return commandItems;
    }
    public void setCommandItems(List<CommandItem> commandItems) {
        this.commandItems = commandItems;
    }
}
