package com.etherea.models;

import jakarta.persistence.*;

@Entity
public class CookieChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String cookieName;
    @Column(nullable = false)
    private boolean accepted;
    @ManyToOne
    @JoinColumn(name = "cookie_consent_id", nullable = false)
    private CookieConsent cookieConsent;
    public CookieChoice() {}
    public CookieChoice(String cookieName, boolean accepted) {
        this.cookieName = cookieName;
        this.accepted = accepted;
    }
    public CookieChoice(String cookieName, boolean accepted, CookieConsent cookieConsent) {
        this.cookieName = cookieName;
        this.accepted = accepted;
        this.cookieConsent = cookieConsent;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public CookieConsent getCookieConsent() {
        return cookieConsent;
    }
    public void setCookieConsent(CookieConsent cookieConsent) {
        this.cookieConsent = cookieConsent;
    }
    public String getCookieName() {
        return cookieName;
    }
    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
    public boolean isAccepted() {
        return accepted;
    }
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
