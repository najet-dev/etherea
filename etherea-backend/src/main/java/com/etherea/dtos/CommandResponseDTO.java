package com.etherea.dtos;

import com.etherea.enums.CommandStatus;
import com.etherea.models.Command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommandResponseDTO {
    private Long id;
    private LocalDateTime commandDate;
    private String referenceCode;
    private CommandStatus status;
    private BigDecimal total;
    public CommandResponseDTO(Long id, LocalDateTime commandDate, String referenceCode, CommandStatus status, BigDecimal total) {
        this.id = id;
        this.commandDate = commandDate;
        this.referenceCode = referenceCode;
        this.status = status;
        this.total = total;
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
    public String getReferenceCode() {
        return referenceCode;
    }
    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }
    public CommandStatus getStatus() {
        return status;
    }
    public void setStatus(CommandStatus status) {
        this.status = status;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public static CommandResponseDTO fromEntity(Command command) {
        return new CommandResponseDTO(command.getId(), command.getCommandDate(),
                command.getReferenceCode(), command.getStatus(), command.getTotal());
    }
}
