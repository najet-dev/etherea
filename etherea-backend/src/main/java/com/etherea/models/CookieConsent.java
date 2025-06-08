package com.etherea.models;

import com.etherea.enums.CookiePolicyVersion;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "cookie_consent", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "sessionId"}) // Composite constraint
})
public class CookieConsent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = true)
    private Long userId;
    @Column(nullable = true)
    private String sessionId;
    @Column(nullable = false)
    private boolean essentialCookies = true;
    @Column(nullable = false)
    private LocalDateTime consentDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CookiePolicyVersion cookiePolicyVersion;
    @OneToMany(mappedBy = "cookieConsent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CookieChoice> cookies = new ArrayList<>();
    public CookieConsent() {}

    // For logged-in users
    public CookieConsent(Long userId, CookiePolicyVersion cookiePolicyVersion, List<CookieChoice> cookies) {
        this.userId = userId;
        this.cookiePolicyVersion = cookiePolicyVersion;
        this.consentDate = LocalDateTime.now();
        setCookies(cookies);
    }
    // For anonymous users
    public CookieConsent(String sessionId, CookiePolicyVersion cookiePolicyVersion, List<CookieChoice> cookies) {
        this.sessionId = sessionId;
        this.cookiePolicyVersion = cookiePolicyVersion;
        this.consentDate = LocalDateTime.now();
        setCookies(cookies);
    }
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
    public void setId(Long id) {
        this.id = id;
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
    public void setEssentialCookies(boolean essentialCookies) {
        this.essentialCookies = essentialCookies;
    }
    public LocalDateTime getConsentDate() {
        return consentDate;
    }
    public void setConsentDate(LocalDateTime consentDate) {
        this.consentDate = consentDate;
    }
    public CookiePolicyVersion getCookiePolicyVersion() {
        return cookiePolicyVersion;
    }
    public void setCookiePolicyVersion(CookiePolicyVersion cookiePolicyVersion) {
        this.cookiePolicyVersion = cookiePolicyVersion;
    }
    public List<CookieChoice> getCookies() {
        return Collections.unmodifiableList(cookies);
    }
}
