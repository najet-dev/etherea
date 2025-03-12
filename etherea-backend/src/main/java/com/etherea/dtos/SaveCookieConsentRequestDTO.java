package com.etherea.dtos;

import com.etherea.enums.CookiePolicyVersion;

import java.util.List;

public class SaveCookieConsentRequestDTO {
    private Long userId;
    private String sessionId;
    private CookiePolicyVersion cookiePolicyVersion;
    private List<CookieChoiceDTO> cookieChoices;
    public SaveCookieConsentRequestDTO() {}
    public SaveCookieConsentRequestDTO(Long userId, String sessionId, CookiePolicyVersion cookiePolicyVersion, List<CookieChoiceDTO> cookieChoices) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.cookiePolicyVersion = cookiePolicyVersion;
        this.cookieChoices = cookieChoices;
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
    public CookiePolicyVersion getCookiePolicyVersion() {
        return cookiePolicyVersion;
    }
    public void setCookiePolicyVersion(CookiePolicyVersion cookiePolicyVersion) {
        this.cookiePolicyVersion = cookiePolicyVersion;
    }
    public void setCookieChoices(List<CookieChoiceDTO> cookieChoices) {
        this.cookieChoices = cookieChoices;
    }
    public List<CookieChoiceDTO> getCookieChoices() {
        return cookieChoices;
    }
}