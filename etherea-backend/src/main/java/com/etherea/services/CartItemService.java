package com.etherea.services;

import com.etherea.dtos.CartItemDTO;
import com.etherea.enums.ProductType;
import com.etherea.exception.CartItemNotFoundException;
import com.etherea.exception.VolumeNotFoundException;
import com.etherea.models.*;
import com.etherea.dtos.ProductDTO;
import com.etherea.dtos.UserDTO;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.repositories.CartItemRepository;
import com.etherea.repositories.ProductRepository;
import com.etherea.repositories.UserRepository;
import com.etherea.repositories.VolumeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    @Autowired
    private VolumeRepository volumeRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartItemService.class);
    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        List<CartItem> cartItems = user.getCartItems();
        logger.info("Cart items for user {}: {}", userId, cartItems);
        logger.info("Nombre d'articles dans le panier pour l'utilisateur {}: {}", userId, cartItems.size());

        // Log des types de produits avant le filtrage
        cartItems.forEach(cartItem ->
                logger.info("Product type before filtering: {}", cartItem.getProduct().getType()));

        // Filtrage des items de type HAIR ou FACE
        cartItems = cartItems.stream()
                .filter(cartItem ->
                        cartItem.getProduct() != null &&
                                (ProductType.HAIR.equals(cartItem.getProduct().getType()) ||
                                        ProductType.FACE.equals(cartItem.getProduct().getType())))
                .collect(Collectors.toList());


        logger.info("Filtered cart items count for user {}: {}", userId, cartItems.size());

        // Log des articles filtrés
        cartItems.forEach(cartItem ->
                logger.info("Filtered product: {}", cartItem.getProduct().getType()));

        // Conversion en DTO
        List<CartItemDTO> cartItemDTOs = cartItems.stream()
                .map(CartItemDTO::fromCartItem)
                .collect(Collectors.toList());

        return cartItemDTOs;
    }
    @Transactional
    public void addProductToUserCart(Long userId, Long productId, Long volumeId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec l'ID : " + productId));

        logger.info("User found: {}, Product found: {}", user, product);

        CartItem existingCartItem = null;

        // Vérification du type de produit
        if (product.getType() == ProductType.HAIR) {
            if (volumeId == null) {
                throw new IllegalArgumentException("Les produits de type HAIR nécessitent un volume.");
            }

            Volume volume = volumeRepository.findById(volumeId)
                    .orElseThrow(() -> new VolumeNotFoundException("Volume non trouvé avec l'ID : " + volumeId));

            logger.info("Volume found: {}", volume);
            existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, volume);
        } else if (product.getType() == ProductType.FACE) {
            if (volumeId != null) {
                throw new IllegalArgumentException("Les produits de type FACE ne doivent pas avoir de volume.");
            }
            existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, null);
        } else {
            throw new IllegalArgumentException("Le type de produit " + product.getType() + " n'est pas pris en charge.");
        }

        // Ajouter ou mettre à jour l'élément du panier
        if (existingCartItem != null) {
            BigDecimal newQuantity = BigDecimal.valueOf(existingCartItem.getQuantity()).add(BigDecimal.valueOf(quantity));
            existingCartItem.setQuantity(newQuantity.intValue());
            logger.info("Updated existing cart item quantity to {}", newQuantity);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setVolume(product.getType() == ProductType.HAIR ? volumeRepository.findById(volumeId).orElse(null) : null);
            newCartItem.setQuantity(quantity);

            // Calculer le sous-total pour les produits FACE
            BigDecimal subTotal;
            if (product.getType() == ProductType.FACE) {
                subTotal = product.getBasePrice().multiply(BigDecimal.valueOf(quantity));
            } else {
                Volume volume = volumeRepository.findById(volumeId).orElseThrow(() -> new VolumeNotFoundException("Volume non trouvé"));
                subTotal = volume.getPrice().multiply(BigDecimal.valueOf(quantity));
            }

            newCartItem.setSubTotal(subTotal);
            logger.info("Adding new cart item: {}", newCartItem);

            try {
                cartItemRepository.save(newCartItem);
                logger.info("New cart item saved successfully.");
            } catch (Exception e) {
                logger.error("Error saving cart item: {}", e.getMessage());
                throw new RuntimeException("Error saving cart item", e);
            }
        }

        // Mettre à jour le total du panier
        updateCartTotal(userId);
        logger.info("Cart total updated.");
    }

    // Méthode pour mettre à jour le total
    private void updateCartTotal(Long userId) {
        List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
        BigDecimal cartTotal = CartItem.calculateTotalPrice(userCartItems);

        for (CartItem cartItem : userCartItems) {
            cartItem.setTotal(cartTotal);
            cartItem.setSubTotal(cartItem.calculateSubtotal()); // Assurez-vous que cette méthode est correctement implémentée
            logger.info("Saving cart item: {}", cartItem);
            cartItemRepository.save(cartItem);
        }

        logger.info("Cart total updated to {}", cartTotal);
    }
    @Transactional
    public void updateCartItemQuantity(Long userId, Long productId, Long volumeId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec l'ID : " + productId));

        // Vérification pour les produits de type HAIR
        Volume volume = null;
        if (product.getType() == ProductType.HAIR) {
            volume = volumeRepository.findById(volumeId)
                    .orElseThrow(() -> new VolumeNotFoundException("Volume non trouvé avec l'ID : " + volumeId));
        } else if (product.getType() == ProductType.FACE && volumeId != null) {
            throw new IllegalArgumentException("Les produits de type FACE ne doivent pas avoir de volume.");
        }

        CartItem existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, volume);

        if (existingCartItem != null) {
            logger.info("Mise à jour de la quantité pour l'article du panier : {}", existingCartItem);

            // Mise à jour de la quantité de l'élément de panier
            existingCartItem.setQuantity(quantity);

            // Recalcul du sous-total
            BigDecimal subtotal = existingCartItem.calculateSubtotal();
            existingCartItem.setSubTotal(subtotal);

            // Sauvegarde de l'élément de panier mis à jour
            logger.info("Enregistrement de l'élément de panier avec la nouvelle quantité : {}", quantity);
            cartItemRepository.save(existingCartItem);

            // Mettre à jour le total du panier
            updateCartTotal(userId);
            logger.info("Le total du panier a été mis à jour.");
        } else {
            logger.error("Élément de panier non trouvé pour l'utilisateur avec l'ID {} et le produit avec l'ID {}", userId, productId);
            throw new CartItemNotFoundException("Élément de panier non trouvé pour l'utilisateur avec l'ID " + userId + " et le produit avec l'ID " + productId);
        }
    }
    public void deleteCartItem(Long id) {
        Optional<CartItem> existingCartItem = cartItemRepository.findById(id);

        if (existingCartItem.isPresent()) {
            // Supprime l'article de panier spécifique
            CartItem cartItemToDelete = existingCartItem.get();
            Long userId = cartItemToDelete.getUser().getId();
            cartItemRepository.deleteById(id);

            // Mettre à jour le total et le sous-total
            List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
            BigDecimal cartTotal = CartItem.calculateTotalPrice(userCartItems);

            // Mettre à jour le champ total pour tous les articles restants
            for (CartItem cartItem : userCartItems) {
                cartItem.setTotal(cartTotal);
                cartItem.setSubTotal(cartItem.calculateSubtotal());
                cartItemRepository.save(cartItem);
            }
        } else {
            // Si l'article de panier n'existe pas, lever une exception
            logger.warn("CartItem with id {} not found", id);
            throw new CartItemNotFoundException("CartItem with id " + id + " not found");
        }
    }

}