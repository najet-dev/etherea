package com.etherea.controllers;

import com.etherea.exception.ProductNotFoundException;
import com.etherea.models.Product;
import com.etherea.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;

@CrossOrigin()
@RestController
public class ProductController {

    @Autowired
    ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping(value = "/products")
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }
    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return productService.getProductById(id);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping(value = "/product/add", consumes = "multipart/form-data")
    public ResponseEntity<String> saveProduct(@RequestParam("image") MultipartFile file,
                                              @RequestParam("product") String productJson) {
        try {
            logger.info("Request Body: {}", productJson);

            ObjectMapper objectMapper = new ObjectMapper();
            Product product = objectMapper.readValue(productJson, Product.class);
            productService.saveProduct(product, file);

            return ResponseEntity.ok("Product saved successfully");

        } catch (JsonProcessingException e) {
            logger.error("JSON deserialization error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JSON deserialization error: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error reading file", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error reading file: " + e.getMessage());
        }
    }

    @PutMapping(value = "/product/update/{productId}", consumes = "multipart/form-data")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId,
                                                @RequestParam("image") MultipartFile file,
                                                @RequestParam("product") String updatedProductJson) {
        try {
            logger.info("Request Body: {}", updatedProductJson);

            ObjectMapper objectMapper = new ObjectMapper();
            Product updatedProduct = objectMapper.readValue(updatedProductJson, Product.class);
            productService.updateProduct(productId, updatedProduct, file);

            return ResponseEntity.ok("Product updated successfully");
        } catch (JsonProcessingException e) {
            logger.error("JSON deserialization error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JSON deserialization error: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error reading file", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error reading file: " + e.getMessage());
        }
    }
    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
