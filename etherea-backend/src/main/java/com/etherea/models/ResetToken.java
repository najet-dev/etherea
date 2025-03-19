package com.etherea.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private LocalDateTime expiryDate;
    private boolean used = false;
    public ResetToken() {}
    public ResetToken(User user) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(15);
    }
    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    public boolean isUsed() {
        return used;
    }
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    public void markAsUsed() {
        this.used = true;
    }
    public void refreshToken() {
        this.token = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusMinutes(15);
        this.used = false;
    }
}
