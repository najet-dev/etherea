package com.etherea.repositories;

import com.etherea.models.DeliveryMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
    List<DeliveryMethod> findByUserId(Long userId);
}
