package com.etherea.services;

import com.etherea.dtos.CartItemDTO;
import com.etherea.models.CartItem;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        List<CartItem> cartItems = user.getCartItems();
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            cartItemDTOs.add(CartItemDTO.fromCartItem(cartItem));
        }
        return cartItemDTOs;
    }

    // Méthode pour ajouter un produit au panier d'un utilisateur
    @PreAuthorize("isAuthenticated()")
    public void addProductToUserCart(Long userId, Long productId, int quantity) {
        // Recherche de l'utilisateur dans la base de données
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        // Recherche du produit dans la base de données
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec l'ID : " + productId));

        // Vérifier si le produit est déjà dans le panier de l'utilisateur
        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);
        if (existingCartItem != null) {
            // Mettre à jour la quantité du produit dans le panier
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
        } else {
            // Créer un nouvel élément de panier
            existingCartItem = new CartItem();
            existingCartItem.setUser(user);
            existingCartItem.setProduct(product);
            existingCartItem.setQuantity(quantity);
            // Ajouter le nouvel élément de panier à la liste des éléments de panier de l'utilisateur
            user.getCartItems().add(existingCartItem);
        }
        // Sauvegarder les changements dans le panier
        cartItemRepository.save(existingCartItem);

        // Recalculer le sous-total et le total uniquement pour l'élément de panier modifié ou nouvellement créé
        existingCartItem.setSubTotal(existingCartItem.calculateSubtotal());
        existingCartItem.setTotal(existingCartItem.calculateTotal());
    }

    public double calculateCartTotal(Long userId) {
        // Recherche de l'utilisateur dans la base de données
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Récupération des éléments du panier de l'utilisateur
        List<CartItem> cartItems = user.getCartItems();

        // Initialisation du total
        double cartTotal = 0.0;

        // Calcul du total en parcourant tous les éléments du panier
        for (CartItem cartItem : cartItems) {
            cartTotal += cartItem.calculateSubtotal();
        }
        return cartTotal;
    }
}
