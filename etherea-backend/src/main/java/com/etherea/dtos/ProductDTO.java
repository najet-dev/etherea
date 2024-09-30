package com.etherea.dtos;

import com.etherea.enums.ProductType;
import com.etherea.enums.StockStatus;
import com.etherea.models.Product;
import com.etherea.models.Volume;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)  // Ignorer les champs inconnus lors de la désérialisation
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private ProductType type;
    private BigDecimal basePrice;
    private StockStatus stockStatus;
    private String benefits;
    private String usageTips;
    private String ingredients;
    private String characteristics;
    private String image;
    private List<VolumeDTO> volumes;

    // Constructeurs
    public ProductDTO() {}

    public ProductDTO(Long id, String name, String description, ProductType type, BigDecimal basePrice, StockStatus stockStatus,
                      String benefits, String usageTips, String ingredients, String characteristics,
                      String image, List<VolumeDTO> volumes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.basePrice = basePrice;
        this.stockStatus = stockStatus;
        this.benefits = benefits;
        this.usageTips = usageTips;
        this.ingredients = ingredients;
        this.characteristics = characteristics;
        this.image = image;
        this.volumes = volumes;
    }
    // Getters et Setters
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
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
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
    public List<VolumeDTO> getVolumes() {
        return volumes;
    }
    public void setVolumes(List<VolumeDTO> volumes) {
        this.volumes = volumes;
    }

    // Convertir un objet Product en ProductDTO
    public static ProductDTO fromProduct(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getType(),
                product.getBasePrice(),
                product.getStockStatus(),
                product.getBenefits(),
                product.getUsageTips(),
                product.getIngredients(),
                product.getCharacteristics(),
                product.getImage(),
                product.getVolumes() != null ? product.getVolumes().stream()
                        .map(VolumeDTO::fromVolume)
                        .collect(Collectors.toList()) : null
        );
    }
    // Convertir un objet ProductDTO en Product
    public Product toProduct() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setType(this.type);
        product.setBasePrice(this.basePrice);
        product.setStockStatus(this.stockStatus);
        product.setBenefits(this.benefits);
        product.setUsageTips(this.usageTips);
        product.setIngredients(this.ingredients);
        product.setCharacteristics(this.characteristics);
        product.setImage(this.image);

        if (this.volumes != null) {
            this.volumes.forEach(volumeDTO -> {
                Volume volume = volumeDTO.toVolume();
                volume.setProduct(product);
                product.addVolume(volume);
            });
        }
        return product;
    }
}