package com.etherea.repositories;

import com.etherea.models.PickupPointDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PickupPointDeliveryRepository extends JpaRepository<PickupPointDelivery, Long>{
    List<PickupPointDelivery> findByLatitudeBetweenAndLongitudeBetween(Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude);

}
