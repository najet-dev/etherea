package com.etherea.repositories;

import com.etherea.models.CommandItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandItemRepository  extends JpaRepository<CommandItem, Long> {
    List<CommandItem>findByCommandId(Long commandId);
}