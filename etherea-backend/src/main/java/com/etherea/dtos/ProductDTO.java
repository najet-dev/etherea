package com.etherea.dtos;

import com.etherea.models.Product;

public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private int quantity;
    private double price;
    private int stockAvailable;
    private String benefits;
    private String usageTips;
    private String ingredients;
    private String characteristics;
    private String image;

    public ProductDTO() {
    }
    public ProductDTO(Long id, String name, String description,int quantity, double price, int stockAvailable, String benefits, String usageTips, String ingredients, String characteristics, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockAvailable = stockAvailable;
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

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setPrice(product.getPrice());
        productDTO.setStockAvailable(product.getStockAvailable());
        productDTO.setBenefits(product.getBenefits());
        productDTO.setUsageTips(product.getUsageTips());
        productDTO.setIngredients(product.getIngredients());
        productDTO.setCharacteristics(product.getCharacteristics());
        productDTO.setImage(product.getImage());
        return productDTO;
    }
    public Product toProduct() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setQuantity(this.quantity);
        product.setPrice(this.price);
        product.setStockAvailable(this.stockAvailable);
        product.setBenefits(this.benefits);
        product.setUsageTips(this.usageTips);
        product.setIngredients(this.ingredients);
        product.setCharacteristics(this.characteristics);
        product.setImage(this.image);
        return product;
    }
}
