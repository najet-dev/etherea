package com.etherea.controllers;

import com.etherea.dtos.CookieConsentDTO;
import com.etherea.dtos.SaveCookieConsentRequestDTO;
import com.etherea.enums.CookiePolicyVersion;
import com.etherea.services.CookieConsentService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/cookies")
@CrossOrigin
public class CookieConsentController {
    private final CookieConsentService cookieConsentService;

    public CookieConsentController(CookieConsentService cookieConsentService) {
        this.cookieConsentService = cookieConsentService;
    }

    // Génération et récupération du sessionId
    @GetMapping("/session")
    public ResponseEntity<String> getSessionId(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = getSessionIdFromCookies(request);

        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            Cookie sessionCookie = new Cookie("sessionId", sessionId);
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            sessionCookie.setMaxAge(60 * 60 * 24 * 30); // 30 jours
            response.addCookie(sessionCookie);
        }

        return ResponseEntity.ok(sessionId);
    }

    // Récupérer le consentement utilisateur
    @GetMapping
    public ResponseEntity<CookieConsentDTO> getUserConsent(
            @CookieValue(value = "sessionId", required = false) String sessionId,
            @RequestParam(required = false) Long userId,
            HttpServletRequest request) {

        if (sessionId == null) {
            sessionId = getSessionIdFromCookies(request);
        }

        if (sessionId == null && userId == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(cookieConsentService.getConsent(userId, sessionId));
    }
    @GetMapping("/config")
    public ResponseEntity<Map<String, List<String>>> getCookiesConfig() {
        Map<String, List<String>> cookiesConfig = cookieConsentService.getCookiesConfig();
        return ResponseEntity.ok(cookiesConfig);
    }
    @PostMapping("/accept-all")
    public ResponseEntity<CookieConsentDTO> acceptAllCookies(
            @CookieValue(value = "sessionId", required = false) String sessionId,
            @Valid @RequestBody SaveCookieConsentRequestDTO request,
            HttpServletRequest httpRequest) {

        if (sessionId == null) {
            sessionId = getSessionIdFromCookies(httpRequest);
        }

        request = new SaveCookieConsentRequestDTO(request.getUserId(), sessionId, request.getCookiePolicyVersion(), request.getCookieChoices());
        return ResponseEntity.ok(cookieConsentService.acceptAllCookies(request));
    }
    @PostMapping("/reject-all")
    public ResponseEntity<CookieConsentDTO> rejectAllCookies(
            @CookieValue(value = "sessionId", required = false) String sessionId,
            @Valid @RequestBody SaveCookieConsentRequestDTO request,
            HttpServletRequest httpRequest) {

        if (sessionId == null) {
            sessionId = getSessionIdFromCookies(httpRequest);
        }

        request = new SaveCookieConsentRequestDTO(request.getUserId(), sessionId, request.getCookiePolicyVersion(), request.getCookieChoices());
        return ResponseEntity.ok(cookieConsentService.rejectAllCookies(request));
    }
    @PostMapping("/customize")
    public ResponseEntity<CookieConsentDTO> customizeCookies(
            @CookieValue(value = "sessionId", required = false) String sessionId,
            @Valid @RequestBody SaveCookieConsentRequestDTO request,
            HttpServletRequest httpRequest) {

        if (sessionId == null) {
            sessionId = getSessionIdFromCookies(httpRequest);
        }

        request = new SaveCookieConsentRequestDTO(request.getUserId(), sessionId, request.getCookiePolicyVersion(), request.getCookieChoices());
        return ResponseEntity.ok(cookieConsentService.customizeCookies(request));
    }

    //Méthode pour récupérer le sessionId depuis les cookies
    private String getSessionIdFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}