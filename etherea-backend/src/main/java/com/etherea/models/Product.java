package com.etherea.models;

import com.etherea.enums.ProductType;
import com.etherea.enums.StockStatus;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product") // Nom explicite de la table
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
    private List<ProductVolume> productVolumes = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<CommandItem> commandItems = new ArrayList<>();

    // Constructeur par défaut
    public Product() {
        // Initialiser les listes pour éviter les NullPointerExceptions
        this.productVolumes = new ArrayList<>();
        this.commandItems = new ArrayList<>();
    }

    // Constructeur avec paramètres
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
        this.productVolumes = new ArrayList<>();
        this.commandItems = new ArrayList<>();
    }

    // Getters et setters
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductType getType() {
        return this.type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public StockStatus getStockStatus() {
        return this.stockStatus;
    }

    public void setStockStatus(StockStatus stockStatus) {
        this.stockStatus = stockStatus;
    }

    public String getBenefits() {
        return this.benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getUsageTips() {
        return this.usageTips;
    }

    public void setUsageTips(String usageTips) {
        this.usageTips = usageTips;
    }

    public String getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getCharacteristics() {
        return this.characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<CommandItem> getCommandItems() {
        return this.commandItems;
    }

    public void setCommandItems(List<CommandItem> commandItems) {
        this.commandItems = commandItems;
    }

    public List<ProductVolume> getProductVolumes() {
        return this.productVolumes;
    }

    public void setProductVolumes(List<ProductVolume> productVolumes) {
        this.productVolumes = productVolumes;
    }

    // Méthode pour ajouter un ProductVolume à la liste
    public void addProductVolume(ProductVolume productVolume) {
        this.productVolumes.add(productVolume);
        productVolume.setProduct(this);
    }

    // Méthode pour retirer un ProductVolume de la liste
    public void removeProductVolume(ProductVolume productVolume) {
        this.productVolumes.remove(productVolume);
        productVolume.setProduct(null);
    }
}
