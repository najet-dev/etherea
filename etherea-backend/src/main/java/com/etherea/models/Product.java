package com.etherea.models;

import com.etherea.enums.SkinType;
import jakarta.persistence.*;

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
    @Enumerated(EnumType.STRING)
    private SkinType category;
    @Lob
    private byte[] image;
    @OneToMany(mappedBy = "product")
    private List<CommandItem> commandItems = new ArrayList<>();
    public Product() {
    }
    public Product(String name, String description, double price, int stockAvailable, SkinType category, byte[] image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockAvailable = stockAvailable;
        this.category = category;
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

    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }

    public SkinType getCategory() {
        return category;
    }

    public void setCategory(SkinType category) {
        this.category = category;
    }

    public List<CommandItem> getCommandItems() {
        return commandItems;
    }

    public void setCommandItems(List<CommandItem> commandItems) {
        this.commandItems = commandItems;
    }
}
