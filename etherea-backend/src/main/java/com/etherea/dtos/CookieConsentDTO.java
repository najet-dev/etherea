package com.etherea.dtos;

import com.etherea.models.CookieConsent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CookieConsentDTO {
    private Long id;
    private Long userId;
    private String sessionId;
    private boolean essentialCookies = true;
    private String cookiePolicyVersion;
    private List<CookieChoiceDTO> cookieChoices;
    public CookieConsentDTO() {}
    public CookieConsentDTO(Long id, Long userId, String sessionId, String cookiePolicyVersion, List<CookieChoiceDTO> cookieChoices) {
        this.id = id;
        this.userId = userId;
        this.sessionId = sessionId;
        this.cookiePolicyVersion = cookiePolicyVersion;
        this.cookieChoices = cookieChoices;
    }

    // Conversion Entité to DTO
    public static CookieConsentDTO fromEntity(CookieConsent cookieConsent) {
        Objects.requireNonNull(cookieConsent, "Le consentement des cookies ne peut pas être null.");
        return new CookieConsentDTO(
                cookieConsent.getId(),
                cookieConsent.getUserId(),
                cookieConsent.getSessionId(),
                cookieConsent.getCookiePolicyVersion(),
                cookieConsent.getCookies().stream().map(CookieChoiceDTO::fromEntity).collect(Collectors.toList())
        );
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
    public String getCookiePolicyVersion() {
        return cookiePolicyVersion;
    }
    public List<CookieChoiceDTO> getCookieChoices() {
        return cookieChoices;
    }
}
