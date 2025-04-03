package com.etherea.repositories;

import com.etherea.models.Tip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface TipRepository  extends JpaRepository<Tip, Long> {
}
