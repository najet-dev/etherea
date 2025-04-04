package com.etherea.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class UpdatePasswordRequestDTO {
    @NotBlank(message = "L'ancien mot de passe est requis")
    private String currentPassword;
    @NotBlank(message = "Le nouveau mot de passe est requis")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String newPassword;
    @NotBlank(message = "La confirmation du mot de passe est requise")
    private String confirmPassword;
    private Long userId;
    public UpdatePasswordRequestDTO() {
    }
    public UpdatePasswordRequestDTO(String currentPassword, String newPassword, String confirmPassword, Long userId) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
        this.userId = userId;
    }
    public String getCurrentPassword() {
        return currentPassword;
    }
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
