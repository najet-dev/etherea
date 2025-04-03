package com.etherea.repositories;

import com.etherea.enums.CommandStatus;
import com.etherea.models.Cart;
import com.etherea.models.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandRepository  extends JpaRepository<Command, Long> {
    List<Command> findByUserId(Long userId);
    Optional<Command> findByIdAndUserId(Long commandId, Long userId);
    Optional<Command> findByCartIdAndUserId(Long cartId, Long userId);
    boolean existsByCartId(Long cartId);
}