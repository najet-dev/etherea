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
                .orElseThrow(() -> new ProductNotFoundException("Volume non trouvé avec l'ID : " + volumeId));

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
    public void updateCartItemQuantity(Long userId, Long productId, Long volumeId, int newQuantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec l'ID : " + productId));

        Volume volume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new VolumeNotFoundException("Volume non trouvé avec l'ID : " + volumeId));

        CartItem existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, volume);

        if (existingCartItem != null) {
            // Mise à jour de la quantité de l'élément de panier
            existingCartItem.setQuantity(newQuantity);

            // Recalcul du sous-total et du total
            BigDecimal subtotal = existingCartItem.calculateSubtotal();
            existingCartItem.setSubTotal(subtotal);
            existingCartItem.setTotal(subtotal);

            // Sauvegarde de l'élément de panier mis à jour dans la base de données
            cartItemRepository.save(existingCartItem);

            // Mettre à jour le champ total de tous les produits du panier avec le total recalculé
            List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
            BigDecimal cartTotal = CartItem.calculateTotalPrice(userCartItems);

            for (CartItem cartItem : userCartItems) {
                cartItem.setTotal(cartTotal);
                cartItemRepository.save(cartItem);
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
            logger.warn("CartItem with id {} not found", id);
            throw new CartItemNotFoundException("CartItem with id " + id + " not found");
        }
    }
}
