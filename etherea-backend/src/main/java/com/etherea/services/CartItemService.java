package com.etherea.services;

import com.etherea.dtos.CartDTO;
import com.etherea.dtos.CartItemDTO;
import com.etherea.dtos.CommandRequestDTO;
import com.etherea.dtos.VolumeDTO;
import com.etherea.enums.CommandStatus;
import com.etherea.enums.ProductType;
import com.etherea.exception.CartItemNotFoundException;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.CartNotFoundException;
import com.etherea.exception.VolumeNotFoundException;
import com.etherea.models.*;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.repositories.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;
    @Autowired
    private CommandService commandService;
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
     * If the product already exists in the cart, its quantity is updated by adding the new quantity to the existing one.
     *
     * @param cartItemDTO The DTO containing the details of the cart item to be added, including the user ID, product ID, volume ID (if applicable), and the quantity to add.
     * @throws IllegalArgumentException If the quantity is less than or equal to 0, if there is insufficient stock for the product, or if the product type is inconsistent with the volume.
     * @throws UserNotFoundException If no user is found with the given user ID.
     * @throws ProductNotFoundException If no product is found with the given product ID.
     * @throws VolumeNotFoundException If no volume is found with the given volume ID for hair products.
     */
    @Transactional
    public void addProductToUserCart(CartItemDTO cartItemDTO) {
        if (cartItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0.");
        }

        Long userId = cartItemDTO.getUserId();
        Long productId = cartItemDTO.getProductId();
        VolumeDTO volumeDTO = cartItemDTO.getVolume();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'ID : " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produit introuvable avec l'ID : " + productId));

        // Check stock availability
        if (product.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("Le produit " + product.getName() + " est actuellement en rupture de stock.");
        }

        Volume volume = null;
        if (product.getType() == ProductType.HAIR) {
            if (volumeDTO == null) {
                throw new IllegalArgumentException("Les produits de type HAIR nécessitent un volume.");
            }
            volume = volumeRepository.findById(volumeDTO.getId())
                    .orElseThrow(() -> new VolumeNotFoundException("Volume introuvable avec l'ID : " + volumeDTO.getId()));
        }

        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        CartItem existingCartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, volume);

        int stockRemaining = product.getStockQuantity();

        if (existingCartItem != null) {
            int previousQuantity = existingCartItem.getQuantity();
            int newQuantity = previousQuantity + cartItemDTO.getQuantity();

            // Check stock for new quantity
            if (stockRemaining < (newQuantity - previousQuantity)) {
                throw new IllegalArgumentException(
                        "Stock insuffisant pour le produit " + product.getName() + ". " +
                                "Stock restant : " + stockRemaining + " produits."
                );
            }

            product.setStockQuantity(stockRemaining - (newQuantity - previousQuantity));
            existingCartItem.setQuantity(newQuantity);
            existingCartItem.setSubTotal(calculateSubtotal(product, volume, newQuantity));
        } else {
            // Check stock for new entry
            if (stockRemaining < cartItemDTO.getQuantity()) {
                throw new IllegalArgumentException(
                        "Stock insuffisant pour le produit " + product.getName() + ". " +
                                "Stock restant : " + stockRemaining + " produits."
                );
            }

            product.setStockQuantity(stockRemaining - cartItemDTO.getQuantity());
            CartItem newCartItem = cartItemDTO.toCartItem();
            newCartItem.setProduct(product);
            newCartItem.setVolume(volume);
            newCartItem.setCart(cart);
            newCartItem.setSubTotal(calculateSubtotal(product, volume, cartItemDTO.getQuantity()));
            cartItemRepository.save(newCartItem);
        }

        // Warning for low stock levels (e.g. 5 products or less)
        if (product.getStockQuantity() <= 5) {
            logger.warn("Le stock pour le produit " + product.getName() +
                    " est faible. Il reste " + product.getStockQuantity() + " produits.");
        }

        // Update stock status
        product.updateStockStatus();
        productRepository.save(product);
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
     * Validates the user's cart and creates a command.
     *
     * @param userId The ID of the user.
     */
    @Transactional
    public void validateCartAndCreateOrder(Long userId, Long paymentMethodId) {
        // Retrieve User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Retrieve Cart
        Cart cart = user.getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            throw new CartNotFoundException("The cart is empty or does not exist.");
        }
        if (cart.isUsed()) {
            throw new CartNotFoundException("The cart has already been used for an order.");
        }

        // Retrieve Default Delivery Address
        DeliveryAddress deliveryAddress = user.getDefaultAddress();
        if (deliveryAddress == null) {
            throw new DeliveryAddressNotFoundException("Default delivery address not found for user ID: " + userId);
        }

        // Create CommandRequestDTO
        CommandRequestDTO commandRequestDTO = new CommandRequestDTO();
        commandRequestDTO.setCommandDate(LocalDateTime.now());
        commandRequestDTO.setReferenceCode("CMD" + System.currentTimeMillis());
        commandRequestDTO.setStatus(CommandStatus.PENDING);
        commandRequestDTO.setCart(CartDTO.fromCart(cart, null));
        commandRequestDTO.setTotal(cart.calculateFinalTotal());
        commandRequestDTO.setDeliveryAddressId(deliveryAddress.getId());
        commandRequestDTO.setPaymentMethodId(paymentMethodId);

        // Create Command
        commandService.createCommand(commandRequestDTO);

        // Mark Cart as Used
        cart.setUsed(true);
        cartRepository.save(cart);
    }
    /**
     * Updates the quantity of a specific cart item.
     *
     * @param cartItemDTO The DTO containing the cart item details, including the user ID, product ID, volume ID (if applicable), and the new quantity.
     * @throws IllegalArgumentException If the quantity is less than or equal to 0, or if the product stock is insufficient for the requested quantity.
     * @throws CartItemNotFoundException If the cart item for the specified user, product, and volume (if applicable) cannot be found.
     * @throws UserNotFoundException If no user is found with the given user ID.
     * @throws ProductNotFoundException If no product is found with the given product ID.
     * @throws VolumeNotFoundException If the volume (for hair products) cannot be found with the given volume ID.
     */
    @Transactional
    public void updateCartItemQuantity(CartItemDTO cartItemDTO) {
        if (cartItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        Long userId = cartItemDTO.getUserId();
        Long productId = cartItemDTO.getProductId();
        VolumeDTO volumeDTO = cartItemDTO.getVolume();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        Product product = productRepository.findById(productId)

                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        Volume volume = null;
        if (product.getType() == ProductType.HAIR && volumeDTO != null) {
            volume = volumeRepository.findById(volumeDTO.getId())
                    .orElseThrow(() -> new VolumeNotFoundException("Volume not found with ID: " + volumeDTO.getId()));
        }

        CartItem cartItem = cartItemRepository.findByUserAndProductAndVolume(user, product, volume);
        if (cartItem == null) {
            throw new CartItemNotFoundException("Cart item not found for user ID: " + userId +
                    ", product ID: " + productId + ", and volume ID: " + (volumeDTO != null ? volumeDTO.getId() : "null"));
        }

        int previousQuantity = cartItem.getQuantity();
        int newQuantity = cartItemDTO.getQuantity();

        if (product.getStockQuantity() < (newQuantity - previousQuantity)) {
            throw new IllegalArgumentException("Insufficient stock for product ID: " + productId);
        }

        product.setStockQuantity(product.getStockQuantity() - (newQuantity - previousQuantity));
        cartItem.setQuantity(newQuantity);
        cartItem.setSubTotal(calculateSubtotal(product, volume, newQuantity));

        cartItemRepository.save(cartItem);

        // Update stock status
        product.updateStockStatus();
        productRepository.save(product);
        updateCartTotal(userId);
    }

    /**
     * Deletes a specific cart item from the user's cart.
     *
     * @param id The ID of the cart item to delete.
     * @throws CartItemNotFoundException If no cart item is found with the given ID.
     */
    @Transactional
    public void deleteCartItem(Long id) {
        CartItem cartItemToDelete = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("CartItem with id " + id + " not found"));

        Product product = cartItemToDelete.getProduct();
        int quantity = cartItemToDelete.getQuantity();

        // Restore stock
        product.setStockQuantity(product.getStockQuantity() + quantity);
        product.updateStockStatus(); // Update status
        productRepository.save(product);

        Long userId = cartItemToDelete.getUser().getId();
        cartItemRepository.delete(cartItemToDelete);

        updateCartTotal(userId); // Recalculate shopping cart total
    }
}