package com.etherea.models;

import com.etherea.enums.ProductType;
import com.etherea.enums.StockStatus;
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
    @Enumerated(EnumType.STRING)
    private ProductType type;
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;
    private String benefits;
    private String usageTips;
    private String ingredients;
    private String characteristics;
    private String image;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Volume> volumes = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CommandItem> commandItems = new ArrayList<>();

    public Product() {
    }
    public Product(String name, String description, ProductType type, StockStatus stockStatus, String benefits, String usageTips, String ingredients, String characteristics, String image) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.stockStatus = stockStatus;
        this.benefits = benefits;
        this.usageTips = usageTips;
        this.ingredients = ingredients;
        this.characteristics = characteristics;
        this.image = image;
    }
    // Getters and Setters
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
    public ProductType getType() {
        return type;
    }
    public void setType(ProductType type) {
        this.type = type;
    }
    public StockStatus getStockStatus() {
        return stockStatus;
    }
    public void setStockStatus(StockStatus stockStatus) {
        this.stockStatus = stockStatus;
    }
    public String getBenefits() {
        return benefits;
    }
    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }
    public String getUsageTips() {
        return usageTips;
    }
    public void setUsageTips(String usageTips) {
        this.usageTips = usageTips;
    }
    public String getIngredients() {
        return ingredients;
    }
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    public String getCharacteristics() {
        return characteristics;
    }
    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public List<Volume> getVolumes() {
        return volumes;
    }
    public void setVolumes(List<Volume> volumes) {
        this.volumes = volumes;
    }
    public List<CommandItem> getCommandItems() {
        return commandItems;
    }
    public void setCommandItems(List<CommandItem> commandItems) {
        this.commandItems = commandItems;
    }
    public void addVolume(Volume volume) {
        volumes.add(volume);
        volume.setProduct(this);  // Associe le produit au volume
    }
    public void removeVolume(Volume volume) {
        volumes.remove(volume);
        volume.setProduct(null);  // Désassocie le produit du volume
    }
    // Nouvelle méthode pour remplacer les volumes existants par une nouvelle liste
    public void updateVolumes(List<Volume> newVolumes) {
        this.volumes.clear(); // Vide la liste actuelle

        for (Volume volume : newVolumes) {
            addVolume(volume); // Utilise la méthode addVolume pour s'assurer que le produit est correctement associé
        }
    }
}
