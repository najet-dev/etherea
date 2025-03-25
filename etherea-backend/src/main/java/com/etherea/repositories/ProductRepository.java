package com.etherea.repositories;

import com.etherea.enums.ProductType;
import com.etherea.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByType(ProductType type, Pageable pageable);
    List<Product> findByTypeIn(List<ProductType> types, Pageable pageable);
    Optional<Product> findByName(String name);

}
