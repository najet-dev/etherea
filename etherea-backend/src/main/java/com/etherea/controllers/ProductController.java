package com.etherea.controllers;

import com.etherea.dtos.ProductDTO;
import com.etherea.dtos.UpdateProductDTO;
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

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    public ProductController(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
    }
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getProducts(limit));
    }
    @GetMapping("/type")
    public ResponseEntity<List<ProductDTO>> getProductsByTypeAndPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam ProductType type) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsByType(pageable, type));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(Map.of("product", productService.getProductById(id)));
        } catch (ProductNotFoundException e) {
            logger.error("Product not found: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found with ID: " + id));
        }
    }
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> saveProduct(
            @RequestParam("image") MultipartFile file,
            @RequestParam("product") String productJson) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Image file is required."));
        }

        if (StringUtils.isBlank(productJson)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Product data is required."));
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);

            logger.info("Product received: {}", productDTO);

            productService.saveProduct(productDTO, file);
            return ResponseEntity.ok(Map.of("message", "Product saved successfully"));

        } catch (JsonProcessingException e) {
            logger.error("Error deserializing product JSON", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid product JSON data."));
        } catch (Exception e) {
            logger.error("An unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }
    @PutMapping(value = "/update/{productId}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> updateProduct(
            @PathVariable Long productId,
            @RequestParam(value = "image", required = false) MultipartFile file,
            @RequestParam("product") String updatedProductJson) {
        try {
            logger.info("Request Body: {}", updatedProductJson);

            ObjectMapper objectMapper = new ObjectMapper();
            UpdateProductDTO updatedProductDTO = objectMapper.readValue(updatedProductJson, UpdateProductDTO.class);

            productService.updateProduct(productId, updatedProductDTO, file);
            return ResponseEntity.ok(Map.of("message", "Product updated successfully"));

        } catch (JsonProcessingException e) {
            logger.error("JSON deserialization error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "JSON deserialization error: " + e.getMessage()));
        } catch (ProductNotFoundException e) {
            logger.error("Product not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("An unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (ProductNotFoundException e) {
            logger.error("Product not found: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found with ID: " + id));
        }
    }
}
