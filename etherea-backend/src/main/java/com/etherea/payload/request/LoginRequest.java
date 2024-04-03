package com.etherea.payload.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Username cannot be blank")
    private  String username;
    @NotBlank(message = "Password cannot be blank")
    private String password;

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}