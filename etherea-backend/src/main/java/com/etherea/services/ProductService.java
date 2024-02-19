package com.etherea.services;

import com.etherea.exception.ProductNotFoundException;
import com.etherea.models.Product;
import com.etherea.repositories.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public ResponseEntity<?> getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            System.out.println("Product found: " + product);
            return ResponseEntity.ok(product);
        } else {
            throw new ProductNotFoundException("No products found with ID: " + id);
        }
    }
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    public Product updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    existingProduct.setPrice(updatedProduct.getPrice());
                    existingProduct.setStockAvailable(updatedProduct.getStockAvailable());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}

