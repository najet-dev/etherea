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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    @Transactional
    public Long getCartIdByUserId(Long userId) {
        return cartRepository.findLatestActiveCart(userId)
                .map(Cart::getId)
                .orElseThrow(() -> new CartNotFoundException("No active shopping cart found for the user."));
    }


}
