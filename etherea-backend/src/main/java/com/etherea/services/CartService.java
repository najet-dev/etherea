package com.etherea.services;

import com.etherea.exception.CartNotFoundException;
import com.etherea.repositories.CartRepository;
import com.etherea.models.Cart;
import com.etherea.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final CartRepository cartRepository;
    public final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    public CartService(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }
    /**
     * Retrieves the ID of the most recent active shopping cart for a given user.
     *
     * @param userId the ID of the user whose cart is to be retrieved
     * @return the ID of the latest active cart associated with the user
     * @throws CartNotFoundException if no active cart is found for the specified user
     */
    @Transactional
    public Long getCartIdByUserId(Long userId) {
        return cartRepository.findLatestActiveCart(userId)
                .map(Cart::getId)
                .orElseThrow(() -> new CartNotFoundException("No active shopping cart found for the user."));
    }
}
