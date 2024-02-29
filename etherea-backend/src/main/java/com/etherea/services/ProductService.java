package com.etherea.services;

import com.etherea.exception.ProductNotFoundException;
import com.etherea.models.Product;
import com.etherea.repositories.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.FileSystemUtils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public List<Product> getProducts(int limit) {
        if (limit > 0) {
            // Récupérer un nombre spécifique de produits de manière aléatoire
            Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "id"));
            return productRepository.findAll(pageable).getContent();
        } else {
            // Récupérer tous les produits
            return productRepository.findAll();
        }
    }
    public ResponseEntity<?> getProductById(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    logger.info("Product found: {}", product);
                    return ResponseEntity.ok(product);
                })
                .orElseThrow(() -> {
                    logger.error("No products found with ID: {}", id);
                    return new ProductNotFoundException("No products found with ID: " + id);
                });
    }

    public void saveProduct(Product product, MultipartFile file) {
        createUploadDirectory();

        if (product == null || file == null) {
            throw new IllegalArgumentException("Product and file cannot be null");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        try {
            String uploadDir = "assets"; // Répertoire d'enregistrement des images
            String filePath = Paths.get(uploadDir, fileName).toString();

            // Enregistrez le fichier sur le serveur
            try (InputStream fileInputStream = file.getInputStream()) {
                Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            }

            product.setImage(filePath);
            productRepository.save(product);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUploadDirectory() {
        String uploadDir = "assets";
        File uploadDirFile = new File(uploadDir);

        // Créez le dossier s'il n'existe pas
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
    }

    public void updateProduct(Long productId, Product updatedProduct, MultipartFile file) {
        if (productId == null || updatedProduct == null || file == null) {
            throw new IllegalArgumentException("Product ID, product and file cannot be null");
        }

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("No products found with ID: " + productId));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStockAvailable(updatedProduct.getStockAvailable());

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        try {
            String uploadDir = "assets"; // Répertoire d'enregistrement des images
            String filePath = Paths.get(uploadDir, fileName).toString();

            // Enregistrez le fichier sur le serveur
            try (InputStream fileInputStream = file.getInputStream()) {
                Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            }

            existingProduct.setImage(filePath);
            productRepository.save(existingProduct);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void deleteProduct(Long id) {
        Optional<Product> existingProduct = productRepository.findById(id);

        if (existingProduct.isPresent()) {
            productRepository.deleteById(id);
        } else {
            // Utilisez un Logger au lieu de System.out.println pour des logs plus flexibles
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.warn("Product with id {} not found", id);

            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
    }


}

