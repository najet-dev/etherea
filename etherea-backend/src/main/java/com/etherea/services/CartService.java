package com.etherea.services;

import com.etherea.exception.CartNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.User;
import com.etherea.repositories.CartRepository;
import com.etherea.models.Cart;
import com.etherea.repositories.UserRepository;
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
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    public Long getCartIdByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(Cart::getId)
                .orElseThrow(() -> new CartNotFoundException("No cart found for this user"));
    }

}
