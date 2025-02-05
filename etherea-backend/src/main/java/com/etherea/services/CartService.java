package com.etherea.services;

import com.etherea.exception.CartNotFoundException;
import com.etherea.repositories.CartRepository;
import com.etherea.models.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    public Long getCartIdByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(Cart::getId)
                .orElseThrow(() -> new CartNotFoundException("No cart found for this user"));
    }
}
