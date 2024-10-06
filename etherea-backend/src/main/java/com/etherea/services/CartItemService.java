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

    /**
     * Retrieves a list of cart items for a specific user by their ID.
     * Filters the items to only include products of type HAIR or FACE.
     *
     * @param userId the ID of the user.
     * @return A list of CartItemDTOs.
     */
    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<CartItem> cartItems = user.getCartItems();
        logger.info("Cart items for user {}: {}", userId, cartItems);
        logger.info("Number of items in the cart for user {}: {}", userId, cartItems.size());

        // Log product types before filtering
        cartItems.forEach(cartItem ->
                logger.info("Product type before filtering: {}", cartItem.getProduct().getType()));

        // Filter items of type HAIR or FACE
        cartItems = cartItems.stream()
                .filter(cartItem ->
                        cartItem.getProduct() != null &&
                                (ProductType.HAIR.equals(cartItem.getProduct().getType()) ||
                                        ProductType.FACE.equals(cartItem.getProduct().getType())))
                .collect(Collectors.toList());

        logger.info("Filtered cart items count for user {}: {}", userId, cartItems.size());

        // Log filtered items
        cartItems.forEach(cartItem ->
                logger.info("Filtered product: {}", cartItem.getProduct().getType()));

        // Convert to DTO
        List<CartItemDTO> cartItemDTOs = cartItems.stream()
                .map(CartItemDTO::fromCartItem)
                .collect(Collectors.toList());

        return cartItemDTOs;
    }

    /**
     * Adds a product to the user's cart, taking into account the product's volume and quantity.
     * If the item already exists, it updates the quantity.
     *
     * @param userId the ID of the user.
     * @param productId the ID of the product.
     * @param volumeId  the ID of the volume (if applicable).
     * @param quantity  the quantity to add.
     */
    @Transactional
    public void addProductToUserCart(Long userId, Long productId, Long volumeId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        logger.info("User found: {}, Product found: {}", user, product);

        CartItem existingCartItem = null;

        // Check product type
        if (product.getType() == ProductType.HAIR) {
            if (volumeId == null) {
                throw new IllegalArgumentException("HAIR products require a volume.");
            }

            Volume volume = volumeRepository.findById(volumeId)
                    .orElseThrow(() -> new VolumeNotFoundException("Volume not found with ID: " + volumeId));

            logger.info("Volume found: {}", volume);
            existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, volume);
        } else if (product.getType() == ProductType.FACE) {
            if (volumeId != null) {
                throw new IllegalArgumentException("FACE products should not have a volume.");
            }
            existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, null);
        } else {
            throw new IllegalArgumentException("Unsupported product type: " + product.getType());
        }

        // Add or update cart item
        if (existingCartItem != null) {
            BigDecimal newQuantity = BigDecimal.valueOf(existingCartItem.getQuantity()).add(BigDecimal.valueOf(quantity));
            existingCartItem.setQuantity(newQuantity.intValue());
            existingCartItem.setSubTotal(existingCartItem.calculateSubtotal()); // Update subtotal
            logger.info("Updated existing cart item quantity to {}", newQuantity);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setVolume(product.getType() == ProductType.HAIR ? volumeRepository.findById(volumeId).orElse(null) : null);
            newCartItem.setQuantity(quantity);

            // Calculate subtotal for FACE products
            BigDecimal subTotal;
            if (product.getType() == ProductType.FACE) {
                subTotal = product.getBasePrice().multiply(BigDecimal.valueOf(quantity));
            } else {
                Volume volume = volumeRepository.findById(volumeId).orElseThrow(() -> new VolumeNotFoundException("Volume not found"));
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

        // Update cart total
        updateCartTotal(userId);
        logger.info("Cart total updated.");
    }

    /**
     * Updates the total and subtotal for all items in the user's cart.
     *
     * @param userId the ID of the user.
     */
    private void updateCartTotal(Long userId) {
        List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
        BigDecimal cartTotal = CartItem.calculateTotalPrice(userCartItems);

        for (CartItem cartItem : userCartItems) {
            cartItem.setTotal(cartTotal);
            cartItem.setSubTotal(cartItem.calculateSubtotal()); // Ensure this method is implemented correctly
            logger.info("Saving cart item: {}", cartItem);
            cartItemRepository.save(cartItem);
        }

        logger.info("Cart total updated to {}", cartTotal);
    }

    /**
     * Updates the quantity of a specific cart item in the user's cart.
     *
     * @param userId    the ID of the user.
     * @param productId the ID of the product.
     * @param volumeId  the ID of the volume (if applicable).
     * @param quantity  the new quantity to set.
     */
    @Transactional
    public void updateCartItemQuantity(Long userId, Long productId, Long volumeId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        // Check for HAIR products
        Volume volume = null;
        if (product.getType() == ProductType.HAIR) {
            volume = volumeRepository.findById(volumeId)
                    .orElseThrow(() -> new VolumeNotFoundException("Volume not found with ID: " + volumeId));
        } else if (product.getType() == ProductType.FACE && volumeId != null) {
            throw new IllegalArgumentException("FACE products should not have a volume.");
        }

        CartItem existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, volume);

        if (existingCartItem != null) {
            logger.info("Updating quantity for cart item: {}", existingCartItem);

            // Update cart item quantity
            existingCartItem.setQuantity(quantity);

            // Recalculate subtotal
            BigDecimal subtotal = existingCartItem.calculateSubtotal();
            existingCartItem.setSubTotal(subtotal);

            // Save updated cart item
            logger.info("Saving cart item with new quantity: {}", quantity);
            cartItemRepository.save(existingCartItem);

            // Update cart total
            updateCartTotal(userId);
            logger.info("Cart total updated.");
        } else {
            logger.error("Cart item not found for user ID {} and product ID {}", userId, productId);
            throw new CartItemNotFoundException("Cart item not found for user ID " + userId + " and product ID " + productId);
        }
    }

    /**
     * Deletes a cart item by its ID and updates the cart totals.
     *
     * @param id the ID of the cart item to delete.
     */
    public void deleteCartItem(Long id) {
        Optional<CartItem> existingCartItem = cartItemRepository.findById(id);

        if (existingCartItem.isPresent()) {
            // Delete the specific cart item
            CartItem cartItemToDelete = existingCartItem.get();
            Long userId = cartItemToDelete.getUser().getId();
            cartItemRepository.deleteById(id);

            // Update the cart's total and subtotals
            List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
            BigDecimal cartTotal = CartItem.calculateTotalPrice(userCartItems);

            // Update the total field for all remaining items
            for (CartItem cartItem : userCartItems) {
                cartItem.setTotal(cartTotal);
                cartItem.setSubTotal(cartItem.calculateSubtotal());
                cartItemRepository.save(cartItem);
            }
        } else {
            // If the cart item doesn't exist, throw an exception
            logger.warn("CartItem with id {} not found", id);
            throw new CartItemNotFoundException("CartItem with id " + id + " not found");
        }
    }
}
