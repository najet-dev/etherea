package com.etherea.controllers;

import com.etherea.enums.SkinType;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.models.Product;
import com.etherea.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin()
@RestController
public class ProductController {

    @Autowired
    ProductService productService;

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
    @PostMapping(value = "/product/add")
    @ResponseBody
    public Product addProduct(@RequestBody Product product) throws Exception {
        return this.productService.createProduct(product);
    }
    @PutMapping(value="product/update/{id}" )
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product updated = productService.updateProduct(id, updatedProduct);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/categories")
    public ResponseEntity<List<SkinType>> getCategories() {
        List<SkinType> categories = productService.getCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

}
