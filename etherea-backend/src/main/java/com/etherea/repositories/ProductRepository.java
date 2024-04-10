package com.etherea.repositories;

import com.etherea.enums.ProductType;
import com.etherea.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository  extends JpaRepository<Product, Long> {
    Page<Product> findByType(ProductType type, Pageable pageable);
    //Page<Product> findByTypeAndIsNew(ProductType type, boolean NEW, Pageable pageable);


}
