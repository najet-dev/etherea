package com.etherea.dtos;

import com.etherea.enums.ProductType;
import com.etherea.enums.StockStatus;

import java.math.BigDecimal;
public class UpdateProductDTO {
    private Long id;
    private String name;
    private String description;
    private ProductType type;
    private BigDecimal basePrice;
    private int stockQuantity;
    private StockStatus stockStatus;
    private String benefits;
    private String usageTips;
    private String ingredients;
    private String characteristics;
    private String image;
    public UpdateProductDTO() {
    }
    public UpdateProductDTO(Long id, String name, String description, ProductType type, BigDecimal basePrice, int stockQuantity, StockStatus stockStatus, String benefits, String usageTips, String ingredients, String characteristics, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.basePrice = basePrice;
        this.stockQuantity = stockQuantity;
        this.stockStatus = stockStatus;
        this.benefits = benefits;
        this.usageTips = usageTips;
        this.ingredients = ingredients;
        this.characteristics = characteristics;
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
    public ProductType getType() {
        return type;
    }
    public void setType(ProductType type) {
        this.type = type;
    }
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
    public int getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
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
}