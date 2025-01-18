package com.etherea.controllers;

import com.etherea.dtos.ProductDTO;
import com.etherea.dtos.VolumeDTO;
import com.etherea.enums.ProductType;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService productService;
    @GetMapping
    public List<ProductDTO> getProducts(@RequestParam(defaultValue = "10") int limit) {
        return productService.getProducts(limit);
    }
    @GetMapping("/type")
    public List<ProductDTO> getProductsByTypeAndPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam ProductType type) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getProductsByType(pageable, type);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (ProductNotFoundException e) {
            logger.error("Product not found: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found with ID: " + id);
        }
    }
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<String> saveProduct(
            @RequestParam("image") MultipartFile file,
            @RequestParam("product") String productJson,
            @RequestParam(value = "volumes", required = false) String volumesJson) {

        if (file == null || file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".png")) {
            return ResponseEntity.badRequest().body("Valid PNG image file is required.");
        }
        if (StringUtils.isBlank(productJson)) {
            return ResponseEntity.badRequest().body("Product data is required.");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);

            if (volumesJson != null && !volumesJson.isEmpty()) {
                List<VolumeDTO> volumeDTOs = objectMapper.readValue(volumesJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, VolumeDTO.class));
                productDTO.setVolumes(volumeDTOs);
            }
            return productService.saveProduct(productDTO, file);

        } catch (JsonProcessingException e) {
            logger.error("Error deserializing product or volumes JSON", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid product or volume JSON data.");
        } catch (Exception e) {
            logger.error("An unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
    @PutMapping(value = "/update/{productId}", consumes = "multipart/form-data")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId,
                                                @RequestParam(value = "image", required = false) MultipartFile file,
                                                @RequestParam("product") String updatedProductJson) {
        try {
            logger.info("Request Body: {}", updatedProductJson);

            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO updatedProductDTO = objectMapper.readValue(updatedProductJson, ProductDTO.class);

            productService.updateProduct(productId, updatedProductDTO, file);
            return ResponseEntity.ok("Product updated successfully");

        } catch (JsonProcessingException e) {
            logger.error("JSON deserialization error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("JSON deserialization error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ProductNotFoundException e) {
            logger.error("Product not found: ID {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
}
