package com.etherea.repositories;

import com.etherea.models.Favorite;
import com.etherea.models.HomeStandardDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeStandardDeliveryRepository extends JpaRepository<HomeStandardDelivery, Long> {
}
