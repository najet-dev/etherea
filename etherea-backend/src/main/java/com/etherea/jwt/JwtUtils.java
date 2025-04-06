package com.etherea.jwt;

import com.etherea.models.User;
import com.etherea.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Value("${etherea.app.jwtSecret}")
    private String jwtSecret;
    @Value("${etherea.app.jwtExpirationMs}")
    private long jwtExpirationMs;
    private SecretKey secretKey;
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    public String generateJwtToken(String username, Set<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }
    public String generateJwtToken(User user, long customExpirationMs) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        long expirationTime = customExpirationMs > 0 ? customExpirationMs : jwtExpirationMs;

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(authToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            System.err.println("Token expiré: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token non supporté: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token mal formé: " + e.getMessage());
        } catch (JwtException e) {  // Replacing SignatureException with JwtException
            System.err.println("Erreur JWT: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Token vide ou invalide: " + e.getMessage());
        }
        return false;
    }
}
        return false;
    }
}
