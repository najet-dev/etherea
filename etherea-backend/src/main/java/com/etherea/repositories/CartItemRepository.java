package com.etherea.repositories;

import com.etherea.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartAndProductAndVolume(Cart cart, Product product, Volume volume);
}

