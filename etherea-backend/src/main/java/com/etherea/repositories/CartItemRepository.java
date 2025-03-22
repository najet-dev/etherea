package com.etherea.repositories;

import com.etherea.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart_User_Id(Long userId);
    CartItem findByCartAndProductAndVolume(Cart cart, Product product, Volume volume);
}

