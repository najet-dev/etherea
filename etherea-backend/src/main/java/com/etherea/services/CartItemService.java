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
    @Transactional
    public void addProductToUserCart(Long userId, Long productId, Long volumeId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        // Créer ou récupérer le panier de l'utilisateur
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
            newCartItem.setCart(cart);  // Lier l'élément au panier
            cartItemRepository.save(newCartItem);
        }

        updateCartTotal(userId);
    }
    private BigDecimal calculateSubtotal(Product product, Volume volume, int quantity) {
        if (product.getType() == ProductType.FACE) {
            return product.getBasePrice().multiply(BigDecimal.valueOf(quantity));
        } else if (product.getType() == ProductType.HAIR && volume != null) {
            return volume.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        throw new IllegalArgumentException("Invalid product or volume for subtotal calculation.");
    }
    private void updateCartTotal(Long userId) {
        List<CartItem> userCartItems = cartItemRepository.findByUserId(userId);
        BigDecimal cartTotal = CartItem.calculateTotalPrice(userCartItems);

        userCartItems.forEach(cartItem -> {
            cartItem.setTotal(cartTotal);
            cartItem.setSubTotal(cartItem.calculateSubtotal());
            cartItemRepository.save(cartItem);
        });
    }
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
            updateCartTotal(userId); // Recalculer le total du panier
        } else {
            throw new CartItemNotFoundException("Cart item not found for user ID " + userId + " and product ID " + productId);
        }
    }
    public void deleteCartItem(Long id) {
        CartItem cartItemToDelete = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("CartItem with id " + id + " not found"));

        Long userId = cartItemToDelete.getUser().getId();
        cartItemRepository.delete(cartItemToDelete);

        updateCartTotal(userId); // Recalculer le total du panier après suppression
    }
}
