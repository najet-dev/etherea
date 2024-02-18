package com.etherea.repositories;

import com.etherea.models.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepository  extends JpaRepository<Command, Long> {
}
