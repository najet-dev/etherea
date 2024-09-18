package com.etherea.services;

import com.etherea.dtos.CartItemDTO;
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
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            cartItemDTOs.add(CartItemDTO.fromCartItem(cartItem));
        }
        return cartItemDTOs;
    }
    public void addProductToUserCart(Long userId, Long productId, Long volumeId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec l'ID : " + productId));

        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new VolumeNotFoundException("Volume non trouvé avec l'ID : " + volumeId));

        // Ajouter des logs pour vérifier les objets trouvés
        logger.info("User found: {}", user);
        logger.info("Product found: {}", product);
        logger.info("Volume found: {}", volume);

        CartItem existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, volume);

        if (existingCartItem != null) {
            // Si le produit est déjà dans le panier, mettre à jour la quantité
            BigDecimal newQuantity = BigDecimal.valueOf(existingCartItem.getQuantity()).add(BigDecimal.valueOf(quantity));
            existingCartItem.setQuantity(newQuantity.intValue());
        } else {
            // Si le produit n'est pas dans le panier, créer un nouvel élément de panier
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setVolume(volume);
            newCartItem.setQuantity(quantity);
            existingCartItem = newCartItem;
        }
        // Sauvegarde ou mise à jour de l'élément de panier dans la base de données
        cartItemRepository.save(existingCartItem);

        // Mettre à jour le total dans chaque élément de panier de l'utilisateur
        List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
        BigDecimal cartTotal = CartItem.calculateTotalPrice(userCartItems);

        for (CartItem cartItem : userCartItems) {
            cartItem.setTotal(cartTotal);
            cartItem.setSubTotal(cartItem.calculateSubtotal());
            cartItemRepository.save(cartItem);
        }
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

        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new VolumeNotFoundException("Volume non trouvé avec l'ID : " + volumeId));

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

            // Mettre à jour le champ total de tous les produits du panier
            List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
            BigDecimal cartTotal = CartItem.calculateTotalPrice(userCartItems);

            // Mettez à jour le champ total pour tous les articles
            for (CartItem cartItem : userCartItems) {
                logger.info("Mise à jour du total pour chaque article du panier.");
                cartItem.setTotal(cartTotal);
                cartItemRepository.save(cartItem);
            }

            logger.info("La quantité de l'article du panier a été mise à jour avec succès pour l'utilisateur {}.", userId);
        } else {
            logger.error("Élément de panier non trouvé pour l'utilisateur avec l'ID {} et le produit avec l'ID {}", userId, productId);
            throw new CartItemNotFoundException("Élément de panier non trouvé pour l'utilisateur avec l'ID " + userId + " et le produit avec l'ID " + productId);
        }
    }
    public void deleteCartItem(Long id) {
        Optional<CartItem> existingCart = cartItemRepository.findById(id);

        if (existingCart.isPresent()) {
            cartItemRepository.deleteById(id);
        } else {
            logger.warn("CartItem with id {} not found", id);
            throw new CartItemNotFoundException("CartItem with id " + id + " not found");
        }
    }
}