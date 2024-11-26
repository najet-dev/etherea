package com.etherea.repositories;

import com.etherea.models.HomeExpressDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeExpressDeliveryRepository  extends JpaRepository<HomeExpressDelivery, Long> {
}

