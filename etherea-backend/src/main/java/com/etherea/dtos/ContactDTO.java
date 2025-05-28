package com.etherea.dtos;

import com.etherea.models.Contact;
import com.etherea.models.User;

import java.time.LocalDateTime;
public class ContactDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String subject;
    private String message;
    private LocalDateTime sentAt;
    public ContactDTO() {}
    public ContactDTO(Long id, String firstName, String lastName, String email, String subject, String message, LocalDateTime sentAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.sentAt = sentAt;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public static ContactDTO fromEntity(Contact contact) {
        User user = contact.getUser();

        return new ContactDTO(
                contact.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                contact.getSubject(),
                contact.getMessage(),
                contact.getSentAt()
        );
    }
}
