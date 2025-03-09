package com.etherea.dtos;

import com.etherea.models.CookieConsent;
import com.etherea.models.CookieChoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CookieConsentDTO {
    private Long id;
    private Long userId;
    private boolean essentialCookies = true; // Toujours activé
    private String cookiePolicyVersion;
    private List<CookieChoiceDTO> cookieChoices;
    public CookieConsentDTO() {}
    public CookieConsentDTO(Long id, Long userId, String cookiePolicyVersion, List<CookieChoiceDTO> cookieChoices) {
        this.id = id;
        this.userId = userId;
        this.cookiePolicyVersion = cookiePolicyVersion;
        this.cookieChoices = cookieChoices;
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
    public String getCookiePolicyVersion() {
        return cookiePolicyVersion;
    }
    public void setCookiePolicyVersion(String cookiePolicyVersion) {
        this.cookiePolicyVersion = cookiePolicyVersion;
    }
    public List<CookieChoiceDTO> getCookieChoices() {
        return cookieChoices;
    }
    public void setCookieChoices(List<CookieChoiceDTO> cookieChoices) {
        this.cookieChoices = cookieChoices;
    }

    // Conversion Entité ➝ DTO
    public static CookieConsentDTO fromEntity(CookieConsent cookieConsent) {
        Objects.requireNonNull(cookieConsent, "Le consentement des cookies ne peut pas être null.");
        return new CookieConsentDTO(
                cookieConsent.getId(),
                cookieConsent.getUserId(),
                cookieConsent.getCookiePolicyVersion(),
                cookieConsent.getCookies() != null ?
                        cookieConsent.getCookies().stream().map(CookieChoiceDTO::fromEntity).collect(Collectors.toList()) :
                        null
        );
    }

    // Conversion DTO ➝ Entité
    public CookieConsent toEntity() {
        List<CookieChoice> choices = (this.cookieChoices != null) ?
                this.cookieChoices.stream().map(CookieChoiceDTO::toEntity).collect(Collectors.toList()) :
                new ArrayList<>();

        CookieConsent cookieConsent = new CookieConsent(this.userId, this.cookiePolicyVersion, choices);

        // Assurer la relation bidirectionnelle entre CookieConsent et CookieChoice
        choices.forEach(choice -> choice.setCookieConsent(cookieConsent));

        return cookieConsent;
    }
}
