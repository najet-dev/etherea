package com.etherea.dtos;

import com.etherea.models.User;

public class UpdateEmailRequestDTO {
    private Long userId;
    private String currentEmail;
    private String newEmail;

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getCurrentEmail() {
        return currentEmail;
    }
    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }
    public String getNewEmail() {
        return newEmail;
    }
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
