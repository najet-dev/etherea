package com.etherea.dtos;

public class NewsletterSendDTO {
    private String subject;
    private String content;
    public NewsletterSendDTO() {
    }
    public NewsletterSendDTO(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
