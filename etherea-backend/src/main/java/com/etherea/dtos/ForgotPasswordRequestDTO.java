package com.etherea.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public class ForgotPasswordRequestDTO {
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;
    public ForgotPasswordRequestDTO() {}
    public ForgotPasswordRequestDTO(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
