package com.etherea.dtos;

import java.util.List;

public class SaveCookieConsentRequestDTO {
    private Long userId;
    private String policyVersion;
    private List<CookieChoiceDTO> cookieChoices;
    public SaveCookieConsentRequestDTO() {}
    public SaveCookieConsentRequestDTO(Long userId, String policyVersion, List<CookieChoiceDTO> cookieChoices) {
        this.userId = userId;
        this.policyVersion = policyVersion;
        this.cookieChoices = (cookieChoices != null) ? cookieChoices : List.of();
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getPolicyVersion() {
        return policyVersion;
    }
    public void setPolicyVersion(String policyVersion) {
        this.policyVersion = policyVersion;
    }
    public List<CookieChoiceDTO> getCookieChoices() {
        return cookieChoices;
    }
    public void setCookieChoices(List<CookieChoiceDTO> cookieChoices) {
        this.cookieChoices = (cookieChoices != null) ? cookieChoices : List.of();
    }
}
