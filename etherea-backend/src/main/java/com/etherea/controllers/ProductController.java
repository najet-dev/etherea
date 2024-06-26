package com.etherea.controllers;

import com.etherea.dtos.ProductDTO;
import com.etherea.enums.ProductType;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @GetMapping
    public List<ProductDTO> getProducts(@RequestParam(defaultValue = "10") int limit) {
        return productService.getProducts(limit);
    }
    @GetMapping("/type")
    public List<ProductDTO> getProductsByTypeAndPagination(@RequestParam(defaultValue = "0") int page,
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found with ID: " + id);
        }
    }
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<String> saveProduct(@RequestParam("image") MultipartFile file,
                                              @RequestParam("product") String productJson) {
        try {
            // Check if required parameters are present
            if (file.isEmpty() || StringUtils.isBlank(productJson)) {
                return ResponseEntity.badRequest().body("Required parameters are missing");
            }

            logger.info("Request Body: {}", productJson);

            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);
            productService.saveProduct(productDTO, file);

            return ResponseEntity.ok("Product saved successfully");

        } catch (JsonProcessingException e) {
            logger.error("JSON deserialization error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("JSON deserialization error: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error reading file", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    @PutMapping(value = "/update/{productId}", consumes = "multipart/form-data")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId,
                                                @RequestParam("image") MultipartFile file,
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
        } catch (IOException e) {
            logger.error("Error reading file", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error reading file: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}