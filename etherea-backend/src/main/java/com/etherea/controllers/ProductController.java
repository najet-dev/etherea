package com.etherea.controllers;

import com.etherea.models.Product;
import com.etherea.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> getProduitById(@PathVariable Long id) {
        Optional<Product> optionalProduct = productService.getProductById(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            System.out.println("Produit trouvé : " + product);
            return ResponseEntity.ok(product);
        } else {
            String message = "Aucun produit trouvé avec l'ID : " + id;
            System.out.println(message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
    }

    @PostMapping(value = "/product/add")
    @ResponseBody
    public Product addProduct(@RequestBody Product product) throws Exception {
        return this.productService.createProduct(product);
    }
}
