package com.etherea.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String subject;
    private String message;
    private LocalDateTime sentAt;
    public Contact() {
        this.sentAt = LocalDateTime.now();
    }
    public Contact(User user, String subject, String message) {
        this.user = user;
        this.subject = subject;
        this.message = message;
        this.sentAt = LocalDateTime.now();
    }
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
