package com.etherea.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "cookie_consent", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "sessionId"}) // Contrainte composite
})
public class CookieConsent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = true)
    private Long userId;
    @Column(nullable = true)
    private String sessionId; // Permet d'identifier les utilisateurs anonymes
    @Column(nullable = false)
    private boolean essentialCookies = true; // Toujours activé
    @Column(nullable = false)
    private LocalDateTime consentDate;
    @Column(nullable = false)
    private String cookiePolicyVersion;
    @OneToMany(mappedBy = "cookieConsent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CookieChoice> cookies = new ArrayList<>();
    public CookieConsent() {}

    // Pour les utilisateurs connectés
    public CookieConsent(Long userId, String cookiePolicyVersion, List<CookieChoice> cookies) {
        this.userId = userId;
        this.cookiePolicyVersion = cookiePolicyVersion;
        this.consentDate = LocalDateTime.now();
        setCookies(cookies);
    }
    // Pour les utilisateurs anonymes
    public CookieConsent(String sessionId, String cookiePolicyVersion, List<CookieChoice> cookies) {
        this.sessionId = sessionId;
        this.cookiePolicyVersion = cookiePolicyVersion;
        this.consentDate = LocalDateTime.now();
        setCookies(cookies);
    }
    // Gestion des cookies
    public void addCookieChoice(List<CookieChoice> newChoices) {
        if (newChoices != null) {
            for (CookieChoice choice : newChoices) {
                choice.setCookieConsent(this);
            }
            this.cookies.addAll(newChoices);
        }
    }
    public void setCookies(List<CookieChoice> cookies) {
        this.cookies.clear();
        if (cookies != null) {
            addCookieChoice(cookies);
        }
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
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public boolean isEssentialCookies() {
        return essentialCookies;
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
}
