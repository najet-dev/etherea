package com.etherea.repositories;

import com.etherea.models.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentMethod, Long> {
    PaymentMethod findByTransactionId(String transactionId);
    Optional<PaymentMethod> findByCartId(Long cartId);


}
