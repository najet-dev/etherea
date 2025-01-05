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
import com.etherea.repositories.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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
    @Autowired
    private CartRepository cartRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartItemService.class);

    /**
     * Retrieves all cart items for a specific user.
     * Only cart items related to HAIR or FACE product types are included.
     *
     * @param userId The ID of the user.
     * @return A list of CartItemDTOs representing the user's cart items.
     * @throws UserNotFoundException If no user is found with the given ID.
     */
    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<CartItem> cartItems = user.getCartItems().stream()
                .filter(cartItem -> {
                    ProductType type = cartItem.getProduct().getType();
                    return type == ProductType.HAIR || type == ProductType.FACE;
                })
                .toList();

        logger.info("Filtered cart items for user {}: {}", userId, cartItems.size());

        return cartItems.stream().map(CartItemDTO::fromCartItem).collect(Collectors.toList());
    }

    /**
     * Adds a product to the user's cart.
     * If the product already exists in the cart, its quantity is updated.
     *
     * @param userId   The ID of the user.
     * @param productId The ID of the product.
     * @param volumeId The ID of the volume, if applicable.
     * @param quantity The quantity to add to the cart.
     * @throws IllegalArgumentException If the quantity is less than or equal to 0, or if the product type is inconsistent with the volume.
     * @throws UserNotFoundException If no user is found with the given ID.
     * @throws ProductNotFoundException If no product is found with the given ID.
     * @throws VolumeNotFoundException If no volume is found with the given ID.
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

        // Create or retrieve the user's cart
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        CartItem existingCartItem;
        Volume volume = null;

        if (product.getType() == ProductType.HAIR) {
            if (volumeId == null) {
                throw new IllegalArgumentException("HAIR products require a volume.");
            }
            volume = volumeRepository.findById(volumeId)
                    .orElseThrow(() -> new VolumeNotFoundException("Volume not found with ID: " + volumeId));
        } else if (product.getType() == ProductType.FACE && volumeId != null) {
            throw new IllegalArgumentException("FACE products should not have a volume.");
        }

        existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, volume);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            existingCartItem.setSubTotal(existingCartItem.calculateSubtotal());
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setVolume(volume);
            newCartItem.setQuantity(quantity);
            newCartItem.setSubTotal(calculateSubtotal(product, volume, quantity));
            newCartItem.setCart(cart);  // Link the item to the cart
            cartItemRepository.save(newCartItem);
        }

        updateCartTotal(userId);
    }

    /**
     * Calculates the subtotal for a cart item based on the product, volume, and quantity.
     *
     * @param product The product to calculate the subtotal for.
     * @param volume The volume associated with the product, if applicable.
     * @param quantity The quantity of the product.
     * @return The calculated subtotal for the item.
     * @throws IllegalArgumentException If the product or volume is invalid for subtotal calculation.
     */
    private BigDecimal calculateSubtotal(Product product, Volume volume, int quantity) {
        if (product.getType() == ProductType.FACE) {
            return product.getBasePrice().multiply(BigDecimal.valueOf(quantity));
        } else if (product.getType() == ProductType.HAIR && volume != null) {
            return volume.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        throw new IllegalArgumentException("Invalid product or volume for subtotal calculation.");
    }

    /**
     * Updates the total price of the user's cart.
     *
     * @param userId The ID of the user.
     */
    private void updateCartTotal(Long userId) {
        List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
        BigDecimal cartTotal = CartItem.calculateTotalPrice(userCartItems);

        userCartItems.forEach(cartItem -> {
            cartItem.setTotal(cartTotal);
            cartItem.setSubTotal(cartItem.calculateSubtotal());
            cartItemRepository.save(cartItem);
        });
    }

    /**
     * Updates the quantity of a specific cart item.
     *
     * @param userId   The ID of the user.
     * @param productId The ID of the product.
     * @param volumeId The ID of the volume, if applicable.
     * @param quantity The new quantity to update to.
     * @throws IllegalArgumentException If the quantity is less than or equal to 0, or if the product type is inconsistent with the volume.
     * @throws CartItemNotFoundException If the cart item cannot be found.
     * @throws UserNotFoundException If no user is found with the given ID.
     * @throws ProductNotFoundException If no product is found with the given ID.
     * @throws VolumeNotFoundException If no volume is found with the given ID.
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

        Volume volume = null;
        if (product.getType() == ProductType.HAIR && volumeId != null) {
            volume = volumeRepository.findById(volumeId)
                    .orElseThrow(() -> new VolumeNotFoundException("Volume not found with ID: " + volumeId));
        }

        CartItem existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, volume);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(quantity);
            existingCartItem.setSubTotal(existingCartItem.calculateSubtotal());
            cartItemRepository.save(existingCartItem);
            updateCartTotal(userId); // Recalculate the cart total
        } else {
            throw new CartItemNotFoundException("Cart item not found for user ID " + userId + " and product ID " + productId);
        }
    }

    /**
     * Deletes a specific cart item from the user's cart.
     *
     * @param id The ID of the cart item to delete.
     * @throws CartItemNotFoundException If no cart item is found with the given ID.
     */
    public void deleteCartItem(Long id) {
        CartItem cartItemToDelete = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("CartItem with id " + id + " not found"));

        Long userId = cartItemToDelete.getUser().getId();
        cartItemRepository.delete(cartItemToDelete);

        updateCartTotal(userId); // Recalculate the cart total after deletion
    }
}
