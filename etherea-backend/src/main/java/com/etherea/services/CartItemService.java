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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     */
    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByCart_User_Id(userId);

        List<CartItem> filteredItems = cartItems.stream()
                .filter(cartItem -> {
                    ProductType type = cartItem.getProduct().getType();
                    return type == ProductType.HAIR || type == ProductType.FACE;
                })
                .toList();

        logger.info("Filtered cart items for user {}: {}", userId, filteredItems.size());

        return filteredItems.stream()
                .map(CartItemDTO::fromCartItem)
                .collect(Collectors.toList());
    }

    /**
     * Adds a product to the user's active cart.
     */
    @Transactional
    public void addProductToUserCart(CartItemDTO cartItemDTO) {
        if (cartItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0.");
        }

        Long userId = cartItemDTO.getUserId();
        Long productId = cartItemDTO.getProductId();
        VolumeDTO volumeDTO = cartItemDTO.getVolume();

        // Vérifier s'il y a plusieurs paniers actifs et désactiver les anciens
        List<Cart> activeCarts = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE);
        if (!activeCarts.isEmpty()) {
            activeCarts.forEach(cart -> cart.setStatus(CartStatus.ORDERED));
            cartRepository.saveAll(activeCarts);
        }

        //Récupérer ou créer un seul panier actif
        Cart cart = cartRepository.findFirstByUserIdAndStatusOrderByIdDesc(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'ID : " + userId));
                    Cart newCart = new Cart(user);
                    newCart.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec l'ID : " + productId));

        Volume volume = null;
        if (product.getType() == ProductType.HAIR) {
            if (volumeDTO == null) {
                throw new IllegalArgumentException("Les produits de type HAIR nécessitent un volume.");
            }
            volume = volumeRepository.findById(volumeDTO.getId())
                    .orElseThrow(() -> new VolumeNotFoundException("Volume introuvable avec l'ID : " + volumeDTO.getId()));
        }

        //Vérifier si le produit est déjà dans le panier
        CartItem existingCartItem = cartItemRepository.findByCartAndProductAndVolume(cart, product, volume);
        if (existingCartItem != null) {
            int newQuantity = existingCartItem.getQuantity() + cartItemDTO.getQuantity();
            if (product.getStockQuantity() < newQuantity - existingCartItem.getQuantity()) {
                throw new IllegalArgumentException("Stock insuffisant pour le produit " + product.getName());
            }
            existingCartItem.setQuantity(newQuantity);
            existingCartItem.setSubTotal(existingCartItem.calculateSubtotal());
        } else {
            if (product.getStockQuantity() < cartItemDTO.getQuantity()) {
                throw new IllegalArgumentException("Stock insuffisant pour le produit " + product.getName());
            }

            CartItem newCartItem = cartItemDTO.toCartItem();
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
     * Updates the total price of the user's cart.
     */
    private void updateCartTotal(Cart cart) {
        cart.calculateTotalAmount();
        cartRepository.save(cart);
    }

    /**
     * Updates the quantity of a specific cart item.
     */
    @Transactional
    public void updateCartItemQuantity(CartItemDTO cartItemDTO) {
        if (cartItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0.");
        }

        Long userId = cartItemDTO.getUserId();
        Long productId = cartItemDTO.getProductId();
        VolumeDTO volumeDTO = cartItemDTO.getVolume();

        Cart cart = cartRepository.findFirstByUserIdAndStatusOrderByIdDesc(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Aucun panier actif trouvé pour l'utilisateur " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec l'ID : " + productId));

        Volume volume = null;
        if (product.getType() == ProductType.HAIR && volumeDTO != null) {
            volume = volumeRepository.findById(volumeDTO.getId())
                    .orElseThrow(() -> new VolumeNotFoundException("Volume introuvable avec l'ID : " + volumeDTO.getId()));
        }

        CartItem cartItem = cartItemRepository.findByCartAndProductAndVolume(cart, product, volume);
        if (cartItem == null) {
            throw new CartItemNotFoundException("Aucun CartItem trouvé pour cet utilisateur et ce produit.");
        }

        int previousQuantity = cartItem.getQuantity();
        int newQuantity = cartItemDTO.getQuantity();

        if (product.getStockQuantity() < (newQuantity - previousQuantity)) {
            throw new IllegalArgumentException("Stock insuffisant pour le produit.");
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
     * Deletes a specific cart item from the user's cart.
     */
    @Transactional
    public void deleteCartItem(Long id) {
        CartItem cartItemToDelete = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("CartItem avec id " + id + " non trouvé"));

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
