package com.etherea.models;

import com.etherea.enums.CommandStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime commandDate;
    private CommandStatus status;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;
    @OneToMany(mappedBy = "command")
    private List<CommandItem> commandItems = new ArrayList<>();
    public Command() {
    }
    public Command(LocalDateTime commandDate, CommandStatus status) {
        this.commandDate = commandDate;
        this.status = status;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDateTime getCommandDate() {
        return commandDate;
    }
    public void setCommandDate(LocalDateTime commandDate) {
        this.commandDate = commandDate;
    }
    public CommandStatus getStatus() {
        return status;
    }
    public void setStatus(CommandStatus status) {
        this.status = status;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public List<CommandItem> getCommandItems() {
        return commandItems;
    }
    public void setCommandItems(List<CommandItem> commandItems) {
        this.commandItems = commandItems;
    }
}
