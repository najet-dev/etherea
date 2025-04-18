package com.etherea.dtos;

import com.etherea.enums.CookiePolicyVersion;
import com.etherea.models.CookieConsent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CookieConsentDTO {
    private Long id;
    private Long userId;
    private String sessionId;
    private boolean essentialCookies = true;
    private CookiePolicyVersion cookiePolicyVersion;
    private List<CookieChoiceDTO> cookieChoices;
    public CookieConsentDTO() {}
    public CookieConsentDTO(Long id, Long userId, String sessionId, CookiePolicyVersion cookiePolicyVersion, List<CookieChoiceDTO> cookieChoices) {
        this.id = id;
        this.userId = userId;
        this.sessionId = sessionId;
        this.cookiePolicyVersion = cookiePolicyVersion;
        this.cookieChoices = cookieChoices;
    }
    public Long getId() {
        return id;
    }
    public Long getUserId() {
        return userId;
    }
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public boolean isEssentialCookies() {
        return essentialCookies;
    }
    public void setEssentialCookies(boolean essentialCookies) {
        this.essentialCookies = essentialCookies;
    }
    public CookiePolicyVersion getCookiePolicyVersion() {
        return cookiePolicyVersion;
    }
    public void setCookiePolicyVersion(CookiePolicyVersion cookiePolicyVersion) {
        this.cookiePolicyVersion = cookiePolicyVersion;
    }
    public List<CookieChoiceDTO> getCookieChoices() {
        return cookieChoices;
    }
    public void setCookieChoices(List<CookieChoiceDTO> cookieChoices) {
        this.cookieChoices = cookieChoices;
    }

    // Entity to DTO conversion
    public static CookieConsentDTO fromEntity(CookieConsent cookieConsent) {
        Objects.requireNonNull(cookieConsent, "Cookie consent cannot be null");
        return new CookieConsentDTO(
                cookieConsent.getId(),
                cookieConsent.getUserId(),
                cookieConsent.getSessionId(),
                cookieConsent.getCookiePolicyVersion(),
                cookieConsent.getCookies().stream().map(CookieChoiceDTO::fromEntity).collect(Collectors.toList())
        );
    }
}
