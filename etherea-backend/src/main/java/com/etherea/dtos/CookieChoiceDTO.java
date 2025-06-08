package com.etherea.dtos;

import com.etherea.models.CookieChoice;
import java.util.Objects;
public class CookieChoiceDTO {
    private String cookieName;
    private boolean accepted;
    public CookieChoiceDTO() {}
    public CookieChoiceDTO(String cookieName, boolean accepted) {
        this.cookieName = cookieName;
        this.accepted = accepted;
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

    // Entity to DTO conversion
    public static CookieChoiceDTO fromEntity(CookieChoice cookieChoice) {
        Objects.requireNonNull(cookieChoice, "Le choix de cookie ne peut pas être null.");
        return new CookieChoiceDTO(cookieChoice.getCookieName(), cookieChoice.isAccepted());
    }
    // DTO to Entity conversion
    public CookieChoice toEntity() {
        return new CookieChoice(this.cookieName, this.accepted);
    }
}
