package com.etherea.dtos;
import com.etherea.models.Newsletter;

public class NewsletterDTO {
    private Long id;
    private String email;
    public NewsletterDTO() { }
    public NewsletterDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public static NewsletterDTO fromEntity(Newsletter newsletter) {
        String email = newsletter.getUser() != null ? newsletter.getUser().getUsername() : null;
        return new NewsletterDTO(newsletter.getId(), email);
    }
}
