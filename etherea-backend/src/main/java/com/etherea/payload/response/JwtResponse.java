package com.etherea.payload.response;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JwtResponse {
    private final String token;
    private final String type;
    private final Long id;
    private final String username;
    private final List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, List<String> roles) {
        // Validate parameters
        Objects.requireNonNull(accessToken, "Access token cannot be null");
        Objects.requireNonNull(id, "User ID cannot be null");
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(roles, "Roles list cannot be null");

        this.token = accessToken;
        this.type = "Bearer"; // Default token type
        this.id = id;
        this.username = username;
        this.roles = Collections.unmodifiableList(roles); // Make roles list immutable
    }
    public String getAccessToken() {
        return token;
    }
    public String getTokenType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    public List<String> getRoles() {
        return roles;
    }
}
