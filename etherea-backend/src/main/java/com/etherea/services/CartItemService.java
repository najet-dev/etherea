package com.etherea.services;

import com.etherea.dtos.CartItemDTO;
import com.etherea.dtos.VolumeDTO;
import com.etherea.enums.CartStatus;
import com.etherea.enums.ProductType;
import com.etherea.exception.*;
import com.etherea.models.*;
import com.etherea.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final VolumeRepository volumeRepository;
    private final CartRepository cartRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartItemService.class);
    public CartItemService(CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository, VolumeRepository volumeRepository, CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.volumeRepository = volumeRepository;
        this.cartRepository = cartRepository;
    }

    /**
     * Retrieves items from a user's active shopping cart
     */
    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        // Find the user's active shopping cart
        Cart activeCart = user.getCarts().stream()
                .filter(cart -> cart.getStatus() == CartStatus.ACTIVE)
                .findFirst()
                .orElse(null);

        if (activeCart == null) {
            return Collections.emptyList();
        }

        // Filter items with the right product types and convert them into DTOs
        return activeCart.getItems().stream()
                .filter(cartItem -> {
                    ProductType type = cartItem.getProduct().getType();
                    return type == ProductType.HAIR || type == ProductType.FACE;
                })
                .map(CartItemDTO::fromCartItem)
                .collect(Collectors.toList());
    }

    /**
     * Adds a product to a user's active shopping cart
     */
    @Transactional
    public void addProductToUserCart(CartItemDTO cartItemDTO) {
        if (cartItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        Long userId = cartItemDTO.getUserId();
        Long productId = cartItemDTO.getProductId();
        VolumeDTO volumeDTO = cartItemDTO.getVolume();

        // Retrieve or create an active shopping cart for the user
        Cart cart = cartRepository.findActiveCartByUser(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("User ID not found : " + userId));
                    Cart newCart = new Cart(user);
                    newCart.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product ID not found : " + productId));

        Volume volume = null;
        if (product.getType() == ProductType.HAIR) {
            if (volumeDTO == null) {
                throw new ProductNotFoundException("HAIR products require a high volume.");
            }
            volume = volumeRepository.findById(volumeDTO.getId())
                    .orElseThrow(() -> new VolumeNotFoundException("Volume not found with ID : " + volumeDTO.getId()));
        }

        // Check if the product is already in the shopping cart
        CartItem existingCartItem = cartItemRepository.findByCartAndProductAndVolume(cart, product, volume);
        if (existingCartItem != null) {
            int newQuantity = existingCartItem.getQuantity() + cartItemDTO.getQuantity();
            if (product.getStockQuantity() < newQuantity - existingCartItem.getQuantity()) {
                throw new ProductNotFoundException("Insufficient stock for the product " + product.getName());
            }
            existingCartItem.setQuantity(newQuantity);
            existingCartItem.setSubTotal(existingCartItem.calculateSubtotal());
        } else {
            if (product.getStockQuantity() < cartItemDTO.getQuantity()) {
                throw new ProductNotFoundException("Insufficient stock for the product " + product.getName());
            }

            CartItem newCartItem = cartItemDTO.toCartItem(product);
            newCartItem.setProduct(product);
            newCartItem.setVolume(volume);
            newCartItem.setCart(cart);
            newCartItem.setSubTotal(newCartItem.calculateSubtotal());
            cartItemRepository.save(newCartItem);
        }

        product.updateStockStatus();
        productRepository.save(product);

        updateCartTotal(cart);
    }

    /**
     * Updates shopping cart total
     */
    private void updateCartTotal(Cart cart) {
        cart.calculateTotalAmount();
        cartRepository.save(cart);
    }

    /**
     * Updates the quantity of an item in the active shopping cart
     */
    @Transactional
    public void updateCartItemQuantity(CartItemDTO cartItemDTO) {
        if (cartItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Long userId = cartItemDTO.getUserId();
        Long productId = cartItemDTO.getProductId();
        VolumeDTO volumeDTO = cartItemDTO.getVolume();

        Cart cart = cartRepository.findActiveCartByUser(userId)
                .orElseThrow(() -> new EntityNotFoundException("No active cart found for user " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID : " + productId));

        Volume volume = null;
        if (product.getType() == ProductType.HAIR && volumeDTO != null) {
            volume = volumeRepository.findById(volumeDTO.getId())
                    .orElseThrow(() -> new VolumeNotFoundException("Volume not found with ID : " + volumeDTO.getId()));
        }

        CartItem cartItem = cartItemRepository.findByCartAndProductAndVolume(cart, product, volume);
        if (cartItem == null) {
            throw new CartItemNotFoundException("No CartItems found for this user and this product.");
        }

        int previousQuantity = cartItem.getQuantity();
        int newQuantity = cartItemDTO.getQuantity();

        if (product.getStockQuantity() < (newQuantity - previousQuantity)) {
            throw new ProductNotFoundException("Insufficient stock for the product.");
        }

        product.setStockQuantity(product.getStockQuantity() - (newQuantity - previousQuantity));
        cartItem.setQuantity(newQuantity);
        cartItem.setSubTotal(cartItem.calculateSubtotal());

        cartItemRepository.save(cartItem);
        product.updateStockStatus();
        productRepository.save(product);

        updateCartTotal(cart);
    }

    /**
     * Deletes an item from the shopping cart
     */
    @Transactional
    public void deleteCartItem(Long id) {
        CartItem cartItemToDelete = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("CartItem with id " + id + " no found"));

        Product product = cartItemToDelete.getProduct();
        int quantity = cartItemToDelete.getQuantity();

        product.setStockQuantity(product.getStockQuantity() + quantity);
        product.updateStockStatus();
        productRepository.save(product);

        Cart cart = cartItemToDelete.getCart();
        cartItemRepository.delete(cartItemToDelete);

        updateCartTotal(cart);
    }
}