package com.etherea.services;

import com.etherea.enums.ProductType;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.models.Product;
import com.etherea.repositories.ProductRepository;
import com.etherea.dtos.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.modelmapper.ModelMapper;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String UPLOAD_DIR = "assets";

    public List<ProductDTO> getProducts(int limit) {
        List<Product> facialProducts = productRepository.findByType(ProductType.FACE, PageRequest.of(0, limit / 2, Sort.by(Sort.Direction.ASC, "id"))).getContent();
        List<Product> hairProducts = productRepository.findByType(ProductType.HAIR, PageRequest.of(0, limit / 2, Sort.by(Sort.Direction.ASC, "id"))).getContent();

        List<Product> allProducts = new ArrayList<>();
        allProducts.addAll(facialProducts);
        allProducts.addAll(hairProducts);

        // Mélanger la liste de tous les produits
        Collections.shuffle(allProducts);

        return allProducts.stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByType(Pageable pageable, ProductType type) {
        Page<Product> productPage = productRepository.findByType(type, pageable);
        return productPage.getContent().stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductDTO::fromProduct)
                .orElseThrow(() -> {
                    logger.error("No product found with ID: {}", id);
                    return new ProductNotFoundException("No product found with ID: " + id);
                });
    }
    public void saveProduct(ProductDTO productDTO, MultipartFile file) {
        createUploadDirectory();

        if (productDTO == null || file == null) {
            throw new IllegalArgumentException("ProductDTO and file cannot be null");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            String filePath = uploadPath.resolve(fileName).toString();

            // Enregistrez le fichier sur le serveur
            try (InputStream fileInputStream = file.getInputStream()) {
                Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            }

            productDTO.setImage(filePath);

            // Convert ProductDTO to Product
            Product product = convertToProduct(productDTO);

            productRepository.save(product);
        } catch (IOException e) {
            logger.error("Error saving product: {}", e.getMessage());
            throw new RuntimeException("Error saving product", e);
        }
    }
    private Product convertToProduct(ProductDTO productDTO) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(productDTO, Product.class);
    }
    private void createUploadDirectory() {
        File uploadDirFile = new File(UPLOAD_DIR);

        // Créez le dossier s'il n'existe pas
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
    }
    public void updateProduct(Long productId, ProductDTO updatedProductDTO, MultipartFile file) {
        if (productId == null || updatedProductDTO == null || file == null) {
            throw new IllegalArgumentException("Product ID, ProductDTO, and file cannot be null");
        }

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("No product found with ID: " + productId));

        // Update product properties from DTO
        updateProductFromDTO(existingProduct, updatedProductDTO);

        // Update product image
        updateProductImage(existingProduct, file);
    }
    private void updateProductFromDTO(Product existingProduct, ProductDTO updatedProductDTO) {
        existingProduct.setName(updatedProductDTO.getName());
        existingProduct.setDescription(updatedProductDTO.getDescription());
        existingProduct.setPrice(updatedProductDTO.getPrice());
        existingProduct.setType(updatedProductDTO.getType());
        existingProduct.setStockAvailable(updatedProductDTO.getStockAvailable());
        existingProduct.setBenefits(updatedProductDTO.getBenefits());
        existingProduct.setUsageTips(updatedProductDTO.getUsageTips());
        existingProduct.setIngredients(updatedProductDTO.getIngredients());
        existingProduct.setCharacteristics(updatedProductDTO.getCharacteristics());
    }
    private void updateProductImage(Product existingProduct, MultipartFile file) {
        if (file.isEmpty()) {
            // Handle empty file case if needed
            return;
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        try {
            String filePath = Paths.get(UPLOAD_DIR, fileName).toString();

            // Enregistrez le fichier sur le serveur
            try (InputStream fileInputStream = file.getInputStream()) {
                Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            }

            existingProduct.setImage(filePath);
            productRepository.save(existingProduct);
        } catch (IOException e) {
            // Gérer l'exception de manière appropriée, par exemple, en lançant une nouvelle exception ou en journalisant l'erreur
            logger.error("Error updating product image: {}", e.getMessage());
            throw new RuntimeException("Error updating product image", e);
        }
    }
    public void deleteProduct(Long id) {
        Optional<Product> existingProduct = productRepository.findById(id);

        if (existingProduct.isPresent()) {
            productRepository.deleteById(id);
        } else {
            logger.warn("Product with id {} not found", id);
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
    }

}