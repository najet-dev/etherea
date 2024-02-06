package com.etherea.models;

import com.etherea.enums.CommandStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime commandDate;
    private CommandStatus status;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;
    @OneToMany(mappedBy = "command")
    private List<CommandItem> commandItems;
}
