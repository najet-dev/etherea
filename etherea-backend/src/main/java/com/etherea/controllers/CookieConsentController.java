package com.etherea.controllers;

import com.etherea.dtos.CookieConsentDTO;
import com.etherea.dtos.SaveCookieConsentRequestDTO;
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

    /**
     * Constructor to initialize the cookie consent service.
     *
     * @param cookieConsentService The service handling cookie consent logic.
     */
    public CookieConsentController(CookieConsentService cookieConsentService) {
        this.cookieConsentService = cookieConsentService;
    }

    /**
     * Generates and retrieves the session ID from cookies. If none exists, a new one is created.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response to store the session ID as a cookie.
     * @return A response containing the session ID.
     */
    @GetMapping("/session")
    public ResponseEntity<Map<String, String>> getSessionId(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = getSessionIdFromCookies(request);

        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            Cookie sessionCookie = new Cookie("sessionId", sessionId);
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            sessionCookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
            response.addCookie(sessionCookie);
        }

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("sessionId", sessionId);

        return ResponseEntity.ok(responseMap);
    }

    /**
     * Retrieves the user's cookie consent based on session ID or user ID.
     *
     * @param sessionId The session ID stored in cookies.
     * @param userId    The user ID (optional).
     * @param request   The HTTP request.
     * @return The user's cookie consent details.
     */
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

    /**
     * Retrieves the configuration of available cookies.
     *
     * @return A map containing cookie categories and their respective cookies.
     */
    @GetMapping("/cookies-list")
    public ResponseEntity<Map<String, List<String>>> getCookiesConfig() {
        Map<String, List<String>> cookiesConfig = cookieConsentService.getCookiesConfig();
        return ResponseEntity.ok(cookiesConfig);
    }

    /**
     * Accepts all cookies for a user session.
     *
     * @param sessionId   The session ID stored in cookies.
     * @param request     The request containing user consent details.
     * @param httpRequest The HTTP request.
     * @return The updated cookie consent details.
     */
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

    /**
     * Rejects all cookies for a user session.
     *
     * @param sessionId   The session ID stored in cookies.
     * @param request     The request containing user consent details.
     * @param httpRequest The HTTP request.
     * @return The updated cookie consent details.
     */
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

    /**
     * Allows users to customize their cookie preferences.
     *
     * @param sessionId   The session ID stored in cookies.
     * @param request     The request containing user consent details.
     * @param httpRequest The HTTP request.
     * @return The updated cookie consent details.
     */
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

    /**
     * Retrieves the session ID from cookies.
     *
     * @param request The HTTP request.
     * @return The session ID if present, otherwise null.
     */
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