package com.etherea.services;

import com.etherea.dtos.CartItemDTO;
import com.etherea.dtos.ProductDTO;
import com.etherea.dtos.UserDTO;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.CartItem;
import com.etherea.models.Product;
import com.etherea.models.User;
import com.etherea.repositories.CartItemRepository;
import com.etherea.repositories.ProductRepository;
import com.etherea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // Méthode pour ajouter un produit au panier d'un utilisateur
    public void addProductToUserCart(Long userId, Long productId, int quantity) {
        // Recherche de l'utilisateur dans la base de données
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            // Recherche du produit dans la base de données
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                User user = userOptional.get();
                Product product = productOptional.get();
                // Créer un nouvel élément de panier
                CartItem cartItem = new CartItem();
                cartItem.setUser(user);
                cartItem.setProduct(product);
                cartItem.setQuantity(quantity);

                cartItemRepository.save(cartItem);

            } else {
                throw new ProductNotFoundException("Product not found with ID: " + productId);
            }
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }


}
