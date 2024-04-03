package com.etherea.repositories;

import com.etherea.models.CartItem;
import com.etherea.models.CommandItem;
import com.etherea.models.Product;
import com.etherea.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByUserAndProduct(User user, Product product);
    List<CartItem> findByUser(User user);
    List<CartItem> findByUserId(Long userId);



}


