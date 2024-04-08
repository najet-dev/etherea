package com.etherea.services;

import com.etherea.dtos.CartItemDTO;
import com.etherea.exception.CartItemNotFoundException;
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
            // Si le produit est déjà dans le panier, mettre à jour la quantité
            int newQuantity = existingCartItem.getQuantity() + quantity;
            existingCartItem.setQuantity(newQuantity);
        } else {
            // Si le produit n'est pas dans le panier, créer un nouvel élément de panier
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            existingCartItem = newCartItem; // Mis à jour pour éviter les confusions
        }

        // Sauvegarde ou mise à jour de l'élément de panier dans la base de données
        cartItemRepository.save(existingCartItem);

        // Mettre à jour le total dans chaque élément de panier de l'utilisateur
        List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
        double cartTotal = 0.0;
        for (CartItem cartItem : userCartItems) {
            double subtotal = cartItem.calculateSubtotal();
            cartItem.setSubTotal(subtotal);
            cartItemRepository.save(cartItem); // Sauvegarde du cartItem avec le sous-total mis à jour
            cartTotal += subtotal;
        }
        // Mettre à jour le champ total de tous les produits du panier avec le total calculé
        for (CartItem cartItem : userCartItems) {
            cartItem.setTotal(cartTotal);
            cartItemRepository.save(cartItem); // Sauvegarde du cartItem avec le total mis à jour
        }
    }
    public void updateCartItemQuantity(Long userId, Long productId, int newQuantity) {
        // Recherche de l'utilisateur dans la base de données
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        // Recherche du produit dans la base de données
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec l'ID : " + productId));

        // Recherche de l'élément de panier correspondant à l'utilisateur et au produit
        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);

        if (existingCartItem != null) {
            // Mise à jour de la quantité de l'élément de panier
            existingCartItem.setQuantity(newQuantity);

            // Recalcul du sous-total et du total
            double subtotal = existingCartItem.calculateSubtotal();
            existingCartItem.setSubTotal(subtotal);
            existingCartItem.setTotal(subtotal); // Total égal au sous-total car un seul article

            // Sauvegarde de l'élément de panier mis à jour dans la base de données
            cartItemRepository.save(existingCartItem);

            // Mettre à jour le champ total de tous les produits du panier avec le total recalculé
            List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
            double cartTotal = 0.0;
            for (CartItem cartItem : userCartItems) {
                cartTotal += cartItem.getSubTotal();
            }
            for (CartItem cartItem : userCartItems) {
                cartItem.setTotal(cartTotal);
                cartItemRepository.save(cartItem); // Sauvegarde du cartItem avec le total mis à jour
            }
        } else {
            throw new CartItemNotFoundException("Élément de panier non trouvé pour l'utilisateur avec l'ID " + userId + " et le produit avec l'ID " + productId);
        }
    }
}