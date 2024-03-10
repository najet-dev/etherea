package com.etherea.repositories;

import com.etherea.models.CartItem;
import com.etherea.models.CommandItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}


