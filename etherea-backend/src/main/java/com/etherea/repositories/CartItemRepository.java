package com.etherea.repositories;

import com.etherea.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByUserAndProduct(User user, Product product);
    CartItem findByUserAndProductAndVolume(User user, Product product, Volume volume);
    List<CartItem> findByUserId(Long userId);
}


