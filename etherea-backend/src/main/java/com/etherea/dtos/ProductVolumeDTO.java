package com.etherea.dtos;

import com.etherea.models.Product;
import com.etherea.models.ProductVolume;

public class ProductVolumeDTO {
    private Long id;
    private String volume;
    private Double price;

    // Constructeur par défaut
    public ProductVolumeDTO() {}

    // Constructeur avec paramètres
    public ProductVolumeDTO(Long id, String volume, Double price) {
        this.id = id;
        this.volume = volume;
        this.price = price;
    }

    // Getters et setters
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVolume() {
        return this.volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Convertit un ProductVolume en ProductVolumeDTO.
     *
     * @param productVolume L'entité ProductVolume à convertir.
     * @return Le ProductVolumeDTO correspondant.
     */
    public static ProductVolumeDTO fromProductVolume(ProductVolume productVolume) {
        if (productVolume == null) {
            return null;
        }
        return new ProductVolumeDTO(
                productVolume.getId(),
                productVolume.getVolume(),
                productVolume.getPrice()
        );
    }

    /**
     * Convertit ce DTO en entité ProductVolume.
     *
     * @param product Le produit associé.
     * @return L'entité ProductVolume correspondante.
     */
    public ProductVolume toProductVolume(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        ProductVolume productVolume = new ProductVolume();
        productVolume.setId(this.id);
        productVolume.setVolume(this.volume);
        productVolume.setPrice(this.price);
        productVolume.setProduct(product);
        return productVolume;
    }
}
