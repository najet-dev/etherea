package com.etherea.controllers;

import com.etherea.dtos.CookieConsentDTO;
import com.etherea.dtos.SaveCookieConsentRequestDTO;
import com.etherea.services.CookieConsentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing user cookie consent.
 * Provides endpoints to accept, reject, or customize cookies.
 */
@RestController
@RequestMapping("/cookies")
public class CookieConsentController {
    private final CookieConsentService cookieConsentService;

    /**
     * Constructor to initialize the CookieConsentService.
     *
     * @param cookieConsentService Service handling cookie consent logic
     */
    public CookieConsentController(CookieConsentService cookieConsentService) {
        this.cookieConsentService = cookieConsentService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CookieConsentDTO> getUserConsent(@PathVariable Long userId) {
        CookieConsentDTO consent = cookieConsentService.getConsentForUser(userId);
        return consent != null ? ResponseEntity.ok(consent) : ResponseEntity.notFound().build();
    }
    @GetMapping("/config")
    public ResponseEntity<Map<String, List<String>>> getCookiesConfig() {
        Map<String, List<String>> cookiesConfig = cookieConsentService.getCookiesConfig();
        return ResponseEntity.ok(cookiesConfig);
    }

    /**
     * Accepts all cookies, including non-essential ones.
     *
     * @param request Contains user ID and cookie policy version
     * @return ResponseEntity containing updated cookie consent details
     */
    @PostMapping("/accept-all")
    public ResponseEntity<CookieConsentDTO> acceptAllCookies(@Valid @RequestBody SaveCookieConsentRequestDTO request) {
        return ResponseEntity.ok(cookieConsentService.acceptAllCookies(request));
    }

    /**
     * Rejects all cookies except essential ones.
     *
     * @param request Contains user ID and cookie policy version
     * @return ResponseEntity containing updated cookie consent details
     */
    @PostMapping("/reject-all")
    public ResponseEntity<CookieConsentDTO> rejectAllCookies(@RequestBody SaveCookieConsentRequestDTO request) {
        return ResponseEntity.ok(cookieConsentService.rejectAllCookies(request));
    }

    /**
     * Allows users to customize their cookie preferences.
     *
     * @param request Contains user ID, cookie policy version, and user-selected cookie preferences
     * @return ResponseEntity containing updated cookie consent details
     */
    @PostMapping("/customize")
    public ResponseEntity<CookieConsentDTO> customizeCookies(@RequestBody SaveCookieConsentRequestDTO request) {
        return ResponseEntity.ok(cookieConsentService.customizeCookies(request));
    }
}
