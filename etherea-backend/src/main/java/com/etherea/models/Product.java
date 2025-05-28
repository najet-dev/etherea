package com.etherea.models;

import com.etherea.enums.ProductType;
import com.etherea.enums.StockStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private ProductType type;
    private BigDecimal basePrice;
    private int stockQuantity;
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;
    private String benefits;
    private String usageTips;
    private String ingredients;
    private String characteristics;
    private String image;
    private boolean newProduct = false;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Volume> volumes = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore // Ignore serialization to avoid recursion problems
    private List<CommandItem> commandItems = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Favorite> favorites = new ArrayList<>();

    public Product() {}
    public Product(String name, String description, ProductType type, BigDecimal basePrice, int stockQuantity, String benefits,
                   String usageTips, String ingredients, String characteristics, String image, boolean newProduct) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.basePrice = basePrice;
        this.stockQuantity = stockQuantity;
        this.benefits = benefits;
        this.usageTips = usageTips;
        this.ingredients = ingredients;
        this.characteristics = characteristics;
        this.image = image;
        this.newProduct = newProduct;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProductType getType() { return type; }
    public void setType(ProductType type) { this.type = type; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    public StockStatus getStockStatus() { return stockStatus; }
    public void setStockStatus(StockStatus stockStatus) { this.stockStatus = stockStatus; }
    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }
    public String getUsageTips() { return usageTips; }
    public void setUsageTips(String usageTips) { this.usageTips = usageTips; }
    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public String getCharacteristics() { return characteristics; }
    public void setCharacteristics(String characteristics) { this.characteristics = characteristics; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public boolean isNewProduct() {
        return newProduct;
    }
    public void setNewProduct(boolean newProduct) {
        this.newProduct = newProduct;
    }

    public List<Volume> getVolumes() { return volumes; }
    public void setVolumes(List<Volume> volumes) { this.volumes = volumes; }
    public List<CommandItem> getCommandItems() { return commandItems; }
    public void setCommandItems(List<CommandItem> commandItems) { this.commandItems = commandItems; }
    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }
    public void addVolume(Volume volume) {
        volumes.add(volume);
        volume.setProduct(this);
    }
    public void removeVolume(Volume volume) {
        volumes.remove(volume);
        volume.setProduct(null);
    }
    public void updateVolumes(List<Volume> newVolumes) {
        this.volumes.clear();
        newVolumes.forEach(this::addVolume);
    }
    public void updateStockStatus() {
        if (this.stockQuantity <= 0) {
            this.stockStatus = StockStatus.OUT_OF_STOCK;
        } else {
            this.stockStatus = StockStatus.AVAILABLE;
        }
    }
}