package com.etherea.repositories;

import com.etherea.models.CookieChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CookieChoiceRepository extends JpaRepository<CookieChoice, Long> {
}
