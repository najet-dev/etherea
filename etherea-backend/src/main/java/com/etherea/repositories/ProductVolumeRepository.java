package com.etherea.repositories;

import com.etherea.models.CartItem;
import com.etherea.models.Product;
import com.etherea.models.ProductVolume;
import com.etherea.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVolumeRepository extends JpaRepository<ProductVolume, Long> {

}
