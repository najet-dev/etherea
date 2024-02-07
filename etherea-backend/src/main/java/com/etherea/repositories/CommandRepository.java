package com.etherea.repositories;

import com.etherea.models.Command;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandRepository  extends JpaRepository<Command, Long> {
}
