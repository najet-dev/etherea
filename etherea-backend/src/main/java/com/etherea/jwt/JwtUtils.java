package com.etherea.jwt;

import com.etherea.controllers.CartItemController;
import com.etherea.models.User;
import com.etherea.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    public String generateJwtToken(String username, Set<String> roles) {
        Set<String> formattedRoles = roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase())
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", formattedRoles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
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
            logger.warn("JWT expired");
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT not supported");
        } catch (MalformedJwtException e) {
            logger.warn("JWT poorly trained");
        } catch (JwtException e) {
            logger.warn("Generic JWT error");
        } catch (IllegalArgumentException e) {
            logger.warn("JWT vide ou null");
        }
        return false;
    }
}