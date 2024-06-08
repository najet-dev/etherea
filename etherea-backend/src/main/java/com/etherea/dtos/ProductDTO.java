package com.etherea.dtos;

import com.etherea.enums.ProductType;
import com.etherea.enums.StockStatus;
import com.etherea.models.Product;
import com.etherea.models.ProductVolume;

import java.util.List;
import java.util.stream.Collectors;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private ProductType type;
    private StockStatus stockStatus;
    private String benefits;
    private String usageTips;
    private String ingredients;
    private String characteristics;
    private String image;
    private List<ProductVolumeDTO> volumes;

    // Constructeur par défaut
    public ProductDTO() {}

    // Constructeur avec paramètres
    public ProductDTO(Long id, String name, String description, ProductType type, StockStatus stockStatus, String benefits, String usageTips, String ingredients, String characteristics, String image, List<ProductVolumeDTO> volumes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.stockStatus = stockStatus;
        this.benefits = benefits;
        this.usageTips = usageTips;
        this.ingredients = ingredients;
        this.characteristics = characteristics;
        this.image = image;
        this.volumes = volumes;
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

    public List<ProductVolumeDTO> getVolumes() {
        return this.volumes;
    }

    public void setVolumes(List<ProductVolumeDTO> volumes) {
        this.volumes = volumes;
    }

    /**
     * Convertit une entité Product en DTO ProductDTO.
     *
     * @param product L'entité Product à convertir.
     * @return Le DTO ProductDTO correspondant.
     */
    public static ProductDTO fromProduct(Product product) {
        if (product == null) {
            return null;
        }

        List<ProductVolumeDTO> volumeDTOs = product.getProductVolumes().stream()
                .map(ProductVolumeDTO::fromProductVolume)
                .collect(Collectors.toList());

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getType(),
                product.getStockStatus(),
                product.getBenefits(),
                product.getUsageTips(),
                product.getIngredients(),
                product.getCharacteristics(),
                product.getImage(),
                volumeDTOs
        );
    }

    /**
     * Convertit ce DTO en entité Product.
     *
     * @return L'entité Product correspondante.
     */
    public Product toProduct() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setType(this.type);
        product.setStockStatus(this.stockStatus);
        product.setBenefits(this.benefits);
        product.setUsageTips(this.usageTips);
        product.setIngredients(this.ingredients);
        product.setCharacteristics(this.characteristics);
        product.setImage(this.image);

        List<ProductVolume> volumes = this.volumes.stream()
                .map(dto -> dto.toProductVolume(product))
                .collect(Collectors.toList());
        product.setProductVolumes(volumes);
        return product;
    }
}
