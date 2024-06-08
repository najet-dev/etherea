package com.etherea.services;

import com.etherea.enums.ProductType;
import com.etherea.enums.StockStatus;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.models.Product;
import com.etherea.models.ProductVolume;
import com.etherea.repositories.ProductRepository;
import com.etherea.dtos.ProductDTO;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String UPLOAD_DIR = "assets";

    /**
     * Récupère une liste de produits avec une limite donnée.
     * @param limit Le nombre maximum de produits à récupérer.
     * @return Une liste de ProductDTO.
     */
    public List<ProductDTO> getProducts(int limit) {
        List<Product> facialProducts = productRepository.findByType(ProductType.FACE, PageRequest.of(0, limit / 2, Sort.by(Sort.Direction.ASC, "id"))).getContent();
        List<Product> hairProducts = productRepository.findByType(ProductType.HAIR, PageRequest.of(0, limit / 2, Sort.by(Sort.Direction.ASC, "id"))).getContent();

        List<Product> allProducts = new ArrayList<>();
        allProducts.addAll(facialProducts);
        allProducts.addAll(hairProducts);

        Collections.shuffle(allProducts);

        return allProducts.stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }
    /**
     * Récupère une liste de produits par type avec pagination.
     * @param pageable Les informations de pagination.
     * @param type Le type de produit à récupérer.
     * @return Une liste de ProductDTO.
     */
    public List<ProductDTO> getProductsByType(Pageable pageable, ProductType type) {
        Page<Product> productPage = productRepository.findByType(type, pageable);
        return productPage.getContent().stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }
    /**
     * Récupère un produit par son ID.
     * @param id L'ID du produit à récupérer.
     * @return Le ProductDTO correspondant.
     */
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductDTO::fromProduct)
                .orElseThrow(() -> {
                    logger.error("No product found with ID: {}", id);
                    return new ProductNotFoundException("No product found with ID: " + id);
                });
    }
    /**
     * Enregistre un nouveau produit.
     * @param productDTO Le DTO du produit à enregistrer.
     * @param file Le fichier image du produit.
     * @return Une ResponseEntity avec le statut de la requête.
     */
    @Transactional
    public ResponseEntity<String> saveProduct(ProductDTO productDTO, MultipartFile file) {
        createUploadDirectory();

        if (productDTO == null || file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("ProductDTO and file cannot be null or empty");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (fileName.contains("..")) {
            return ResponseEntity.badRequest().body("Invalid file name");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            String filePath = uploadPath.resolve(fileName).toString();

            try (InputStream fileInputStream = file.getInputStream()) {
                Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            }

            productDTO.setImage(filePath);

            Product product = productDTO.toProduct();

            productRepository.save(product);

            return ResponseEntity.ok("Product saved successfully");
        } catch (IOException e) {
            logger.error("Error saving product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving product: " + e.getMessage());
        }
    }
    /**
     * Met à jour un produit existant.
     * @param productId L'ID du produit à mettre à jour.
     * @param updatedProductDTO Le DTO du produit mis à jour.
     * @param file Le fichier image du produit.
     */
    @Transactional
    public void updateProduct(Long productId, ProductDTO updatedProductDTO, MultipartFile file) {
        if (productId == null || updatedProductDTO == null) {
            throw new IllegalArgumentException("Product ID and ProductDTO cannot be null");
        }

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("No product found with ID: " + productId));

        updateProductFromDTO(existingProduct, updatedProductDTO);

        if (file != null && !file.isEmpty()) {
            updateProductImage(existingProduct, file);
        }

        productRepository.save(existingProduct);
    }
    /**
     * Met à jour les attributs d'un produit existant à partir d'un ProductDTO.
     * @param existingProduct Le produit existant.
     * @param updatedProductDTO Le DTO contenant les nouvelles valeurs.
     */
    private void updateProductFromDTO(Product existingProduct, ProductDTO updatedProductDTO) {
        existingProduct.setName(updatedProductDTO.getName());
        existingProduct.setDescription(updatedProductDTO.getDescription());
        existingProduct.setType(updatedProductDTO.getType());
        existingProduct.setStockStatus(updatedProductDTO.getStockStatus());
        existingProduct.setBenefits(updatedProductDTO.getBenefits());
        existingProduct.setUsageTips(updatedProductDTO.getUsageTips());
        existingProduct.setIngredients(updatedProductDTO.getIngredients());
        existingProduct.setCharacteristics(updatedProductDTO.getCharacteristics());

        List<ProductVolume> updatedVolumes = updatedProductDTO.getVolumes().stream()
                .map(dto -> dto.toProductVolume(existingProduct))
                .collect(Collectors.toList());

        existingProduct.getProductVolumes().clear();
        existingProduct.getProductVolumes().addAll(updatedVolumes);
    }
    /**
     * Met à jour l'image d'un produit existant.
     * @param existingProduct Le produit existant.
     * @param file Le nouveau fichier image.
     */
    private void updateProductImage(Product existingProduct, MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        try {
            String filePath = Paths.get(UPLOAD_DIR, fileName).toString();

            try (InputStream fileInputStream = file.getInputStream()) {
                Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            }

            existingProduct.setImage(filePath);
        } catch (IOException e) {
            logger.error("Error updating product image: {}", e.getMessage());
            throw new RuntimeException("Error updating product image", e);
        }
    }
    /**
     * Supprime un produit par son ID.
     * @param id L'ID du produit à supprimer.
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

        productRepository.delete(product);
    }
    /**
     * Crée le répertoire de téléchargement si ce n'est pas déjà fait.
     */
    private void createUploadDirectory() {
        File uploadDirFile = new File(UPLOAD_DIR);

        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
    }
}
