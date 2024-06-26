package com.etherea.dtos;

import com.etherea.enums.ProductType;
import com.etherea.enums.StockStatus;
import com.etherea.models.Product;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private ProductType type;
    private StockStatus stockStatus;
    private String benefits;
    private String usageTips;
    private String ingredients;
    private String characteristics;
    private String image;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, String description, double price, ProductType type, StockStatus stockStatus, String benefits, String usageTips, String ingredients, String characteristics, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
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
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
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
    public static ProductDTO fromProduct(Product product) {
        return new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(),product.getType(), product.getStockStatus(), product.getBenefits(), product.getUsageTips(), product.getIngredients(), product.getCharacteristics(), product.getImage());
    }
    public Product toProduct() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setStockStatus(this.stockStatus);
        product.setBenefits(this.benefits);
        product.setUsageTips(this.usageTips);
        product.setIngredients(this.ingredients);
        product.setCharacteristics(this.characteristics);
        product.setImage(this.image);
        return product;
    }
}