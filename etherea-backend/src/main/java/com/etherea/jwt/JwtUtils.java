package com.etherea.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${etherea.app.jwtSecret}")
    private String jwtSecret;

    @Value("${etherea.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    private final SecretKey secretKey;
    public JwtUtils(@Value("${etherea.app.jwtSecret}") String jwtSecret) {
        // Utilisez la méthode Keys.secretKeyFor pour générer une clé HMAC-SHA sécurisée
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }
    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parse(authToken);
            return true;
        } catch (Exception e) {
            // Handle exception, log or ignore based on your requirements
        }
        return false;
    }
}
