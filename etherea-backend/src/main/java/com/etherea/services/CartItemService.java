package com.etherea.services;

import com.etherea.dtos.CartItemDTO;
import com.etherea.exception.CartItemNotFoundException;
import com.etherea.models.*;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.CartItem;
import com.etherea.repositories.CartItemRepository;
import com.etherea.repositories.ProductRepository;
import com.etherea.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartItemService.class);


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
            if (newQuantity <= 0) {
                cartItemRepository.delete(existingCartItem); // Supprimer l'élément si la quantité est nulle ou négative
            } else {
                existingCartItem.setQuantity(newQuantity);
                cartItemRepository.save(existingCartItem);
            }
        } else {
            // Si le produit n'est pas dans le panier, créer un nouvel élément de panier
            if (quantity > 0) { // Ajouter l'élément de panier uniquement si la quantité est positive
                CartItem newCartItem = new CartItem();
                newCartItem.setUser(user);
                newCartItem.setProduct(product);
                newCartItem.setQuantity(quantity);
                cartItemRepository.save(newCartItem);
            }
        }
        // Calculer le total du panier
        List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
        double cartTotal = 0.0;
        for (CartItem cartItem : userCartItems) {
            cartTotal += cartItem.calculateSubTotal();
        }
        // Mettre à jour le total dans tous les éléments de panier de l'utilisateur
        for (CartItem cartItem : userCartItems) {
            cartItem.setTotal(cartTotal);
            cartItemRepository.save(cartItem);
        }
    }
    public void syncCartItems(Long userId, List<CartItemDTO> cartItemDTOs) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + userId));

        List<CartItem> existingCartItems = cartItemRepository.findByUserId(userId);

        double cartTotal = 0.0; // Initialiser le total du panier à 0

        for (CartItemDTO dto : cartItemDTOs) {
            Long productId = dto.getProductId();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec l'ID : " + productId));

            CartItem existingCartItem = existingCartItems.stream()
                    .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingCartItem != null) {
                // Mettre à jour la quantité de l'élément de panier existant
                existingCartItem.setQuantity(dto.getQuantity());
                // Recalculer le sous-total
                double subtotal = product.getPrice() * dto.getQuantity();
                existingCartItem.setSubTotal(subtotal);
                // Sauvegarde de l'élément de panier mis à jour dans la base de données
                cartItemRepository.save(existingCartItem);
            } else {
                // Créer un nouvel élément de panier pour les produits non présents dans le panier existant
                if (dto.getQuantity() > 0) { // Ajouter l'élément de panier uniquement si la quantité est positive
                    CartItem newCartItem = new CartItem();
                    newCartItem.setUser(user);
                    newCartItem.setProduct(product);
                    newCartItem.setQuantity(dto.getQuantity());
                    double subtotal = product.getPrice() * dto.getQuantity();
                    newCartItem.setSubTotal(subtotal);
                    cartItemRepository.save(newCartItem);
                    existingCartItems.add(newCartItem); // Ajouter le nouvel élément de panier à la liste existante
                }
            }
        }

        // Supprimer les éléments de panier existants qui ne sont pas dans la liste reçue depuis le frontend
        existingCartItems.removeIf(cartItem -> !cartItemDTOs.stream()
                .anyMatch(dto -> dto.getProductId().equals(cartItem.getProduct().getId())));
        cartItemRepository.deleteAll(existingCartItems);

        // Recalculer le total du panier après avoir mis à jour les éléments de panier
        cartTotal = existingCartItems.stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();

        // Mettre à jour le total du panier pour tous les éléments de panier
        for (CartItem cartItem : existingCartItems) {
            cartItem.setTotal(cartTotal);
            cartItemRepository.save(cartItem);
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
            // Vérifier si la nouvelle quantité est valide
            if (newQuantity <= 0) {
                cartItemRepository.delete(existingCartItem); // Supprimer l'élément si la quantité est nulle ou négative
            } else {
                // Mise à jour de la quantité de l'élément de panier
                existingCartItem.setQuantity(newQuantity);

                // Recalcul du sous-total et du total
                double subtotal = existingCartItem.calculateSubTotal();
                existingCartItem.setSubTotal(subtotal);
                existingCartItem.setTotal(subtotal); // Total égal au sous-total car un seul article

                // Sauvegarde de l'élément de panier mis à jour dans la base de données
                cartItemRepository.save(existingCartItem);
            }

            // Calculer et mettre à jour le total du panier
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
    public void deleteCartItem(Long id) {
        Optional<CartItem> existingCart = cartItemRepository.findById(id);

        if (existingCart.isPresent()) {
            cartItemRepository.deleteById(id);
        } else {
            logger.warn("Cart with id {} not found", id);
            throw new ProductNotFoundException("Cart with id " + id + " not found");
        }
    }
}