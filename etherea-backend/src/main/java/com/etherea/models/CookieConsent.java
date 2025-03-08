package com.etherea.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class CookieConsent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private  boolean essentialCookies = true;
    @Column(nullable = false)
    private LocalDateTime consentDate;
    @Column(nullable = false)
    private String cookiePolicyVersion;
    @OneToMany(mappedBy = "cookieConsent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CookieChoice> cookies = new ArrayList<>();
    public CookieConsent() {}
    public CookieConsent(Long userId, String cookiePolicyVersion, List<CookieChoice> cookies) {
        this.userId = userId;
        this.cookiePolicyVersion = cookiePolicyVersion;
        this.consentDate = LocalDateTime.now();
        if (cookies != null) {
            addCookieChoice(cookies);
        }
    }
    public void addCookieChoice(List<CookieChoice> newChoices) {
        for (CookieChoice choice : newChoices) {
            choice.setCookieConsent(this);
        }
        this.cookies.addAll(newChoices);
    }
    public Long getId() {
        return id;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public boolean isEssentialCookies() {
        return essentialCookies;
    }
    public void setEssentialCookies(boolean essentialCookies) {
        throw new UnsupportedOperationException("Essential cookies cannot be disabled.");
    }
    public LocalDateTime getConsentDate() {
        return consentDate;
    }
    public void setConsentDate(LocalDateTime consentDate) {
        this.consentDate = consentDate;
    }
    public String getCookiePolicyVersion() {
        return cookiePolicyVersion;
    }
    public void setCookiePolicyVersion(String cookiePolicyVersion) {
        this.cookiePolicyVersion = cookiePolicyVersion;
    }
    public List<CookieChoice> getCookies() {
        return Collections.unmodifiableList(cookies);
    }
    public void setCookies(List<CookieChoice> cookies) {
        this.cookies.clear();
        if (cookies != null) {
            this.cookies.addAll(cookies);
        }
    }

}
