package com.etherea.services;

import com.etherea.dtos.CookieChoiceDTO;
import com.etherea.dtos.CookieConsentDTO;
import com.etherea.dtos.SaveCookieConsentRequestDTO;
import com.etherea.enums.CookiePolicyVersion;
import com.etherea.exception.CookieConsentException;
import com.etherea.models.CookieChoice;
import com.etherea.models.CookieConsent;
import com.etherea.repositories.CookieConsentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
public class CookieConsentService {
    private static final Logger logger = LoggerFactory.getLogger(CookieConsentService.class);
    private final CookieConsentRepository cookieConsentRepository;
    @Value("#{'${cookie.essential}'.split(',')}")
    private List<String> essentialCookies;
    @Value("#{'${cookie.non-essential}'.split(',')}")
    private List<String> nonEssentialCookies;
    public CookieConsentService(CookieConsentRepository cookieConsentRepository) {
        this.cookieConsentRepository = cookieConsentRepository;
    }
    /**
     * Retrieves cookie consent information for a user or session.
     *
     * @param userId    The ID of the user (optional).
     * @param sessionId The session ID (optional).
     * @return The cookie consent details.
     * @throws IllegalArgumentException if neither userId nor sessionId is provided.
     * @throws EntityNotFoundException  if no consent is found.
     */
    @Transactional(readOnly = true)
    public CookieConsentDTO getConsent(Long userId, String sessionId) {
        if (sessionId == null && userId == null) {
            throw new IllegalArgumentException("SessionId ou UserId requis.");
        }

        CookieConsent consent = null;

        if (userId != null) {
            consent = cookieConsentRepository.findByUserId(userId).orElse(null);
        }

        if (consent == null && sessionId != null) {
            consent = cookieConsentRepository.findBySessionId(sessionId).orElse(null);
        }

        if (consent == null) {
            throw new EntityNotFoundException("Aucun consentement trouvé.");
        }

        return CookieConsentDTO.fromEntity(consent);
    }
    /**
     * Retrieves the configuration of essential and non-essential cookies.
     *
     * @return A map containing categorized cookies.
     */
    public Map<String, List<String>> getCookiesConfig() {
        Map<String, List<String>> cookiesConfig = new HashMap<>();
        cookiesConfig.put("essential", essentialCookies);
        cookiesConfig.put("non-essential", nonEssentialCookies);
        return cookiesConfig;
    }

    /**
     * Accepts all cookies for a user or session.
     *
     * @param request The request containing user ID, session ID, and policy version.
     * @return The updated cookie consent details.
     */
    @Transactional
    public CookieConsentDTO acceptAllCookies(SaveCookieConsentRequestDTO request) {
        if (request.getCookiePolicyVersion() == null) {
            throw new CookieConsentException("The version of the cookie policy must be specified");
        }

        CookieConsent consent = getOrCreateConsent(request.getUserId(), request.getSessionId(), request.getCookiePolicyVersion(), new ArrayList<>());
        consent.setCookiePolicyVersion(request.getCookiePolicyVersion());
        consent.setConsentDate(LocalDateTime.now());

        List<CookieChoice> existingChoices = consent.getCookies() != null ? new ArrayList<>(consent.getCookies()) : new ArrayList<>();

        List<String> allCookies = Stream.concat(essentialCookies.stream(), nonEssentialCookies.stream()).toList();

        for (String cookieName : allCookies) {
            Optional<CookieChoice> existingChoiceOpt = existingChoices.stream()
                    .filter(choice -> choice.getCookieName().equals(cookieName))
                    .findFirst();

            if (existingChoiceOpt.isPresent()) {
                // Update existing systems
                existingChoiceOpt.get().setAccepted(true);
            } else {
                //Create only if cookie does not yet exist
                CookieChoice newChoice = new CookieChoice(cookieName, true);
                newChoice.setCookieConsent(consent);
                existingChoices.add(newChoice);
            }
        }
        consent.setCookies(existingChoices);
        cookieConsentRepository.save(consent);

        logger.info("Consent accepted for userId={} or sessionId={}", request.getUserId(), request.getSessionId());

        return CookieConsentDTO.fromEntity(consent);
    }

    /**
     * Rejects all cookies except essential ones.
     *
     * @param request The request containing user ID, session ID, and policy version.
     * @return The updated cookie consent details.
     */
    @Transactional
    public CookieConsentDTO rejectAllCookies(SaveCookieConsentRequestDTO request) {
        if (request.getCookiePolicyVersion() == null) {
            throw new  CookieConsentException("The version of the cookie policy must be specified");
        }

        CookieConsent consent = getOrCreateConsent(request.getUserId(), request.getSessionId(), request.getCookiePolicyVersion(), new ArrayList<>());
        consent.setCookiePolicyVersion(request.getCookiePolicyVersion());
        consent.setConsentDate(LocalDateTime.now());

        List<String> allCookies = Stream.concat(essentialCookies.stream(), nonEssentialCookies.stream())
                .toList();

        List<CookieChoice> updatedChoices = new ArrayList<>();

        for (String cookieName : allCookies) {
            Optional<CookieChoice> existingChoiceOpt = Optional.ofNullable(consent.getCookies())
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(choice -> choice.getCookieName().equals(cookieName))
                    .findFirst();

            if (existingChoiceOpt.isPresent()) {
                existingChoiceOpt.get().setAccepted(essentialCookies.contains(cookieName));
                updatedChoices.add(existingChoiceOpt.get());
            } else {
                CookieChoice newChoice = new CookieChoice(cookieName, essentialCookies.contains(cookieName));
                newChoice.setCookieConsent(consent);
                updatedChoices.add(newChoice);
            }
        }

        consent.setCookies(updatedChoices);
        cookieConsentRepository.save(consent);

        logger.info("Consent rejected for userId={} or sessionId={}", request.getUserId(), request.getSessionId());

        return CookieConsentDTO.fromEntity(consent);
    }

    /**
     * Customizes the user's cookie consent preferences.
     *
     * @param request The request containing the user ID, session ID, cookie policy version, and cookie choices.
     * @return A {@link CookieConsentDTO} representing the updated cookie consent details.
     * @throws CookieConsentException if the cookie policy version is missing or the cookie choices are invalid.
     */
    @Transactional
    public CookieConsentDTO customizeCookies(SaveCookieConsentRequestDTO request) {
        if (request.getCookiePolicyVersion() == null) {
            throw new  CookieConsentException("The version of the cookie policy must be specified");
        }

        if (request.getCookieChoices() == null || request.getCookieChoices().isEmpty()) {
            throw new  CookieConsentException("Cookie selections must not be null or empty");
        }

        CookieConsent consent = getOrCreateConsent(request.getUserId(), request.getSessionId(), request.getCookiePolicyVersion(), new ArrayList<>());
        consent.setCookiePolicyVersion(request.getCookiePolicyVersion());
        consent.setConsentDate(LocalDateTime.now());

        List<CookieChoice> existingChoices = consent.getCookies() != null ? new ArrayList<>(consent.getCookies()) : new ArrayList<>();

        // Process personalized user choices
        for (CookieChoiceDTO newChoice : request.getCookieChoices()) {
            if (!essentialCookies.contains(newChoice.getCookieName()) && !nonEssentialCookies.contains(newChoice.getCookieName())) {
                throw new  CookieConsentException("Invalid cookie name : " + newChoice.getCookieName());
            }

            Optional<CookieChoice> existingChoiceOpt = existingChoices.stream()
                    .filter(choice -> choice.getCookieName().equals(newChoice.getCookieName()))
                    .findFirst();

            if (existingChoiceOpt.isPresent()) {
                CookieChoice existingChoice = existingChoiceOpt.get();
                existingChoice.setAccepted(newChoice.isAccepted());
            } else {
                CookieChoice newCookieChoice = new CookieChoice(newChoice.getCookieName(), newChoice.isAccepted());
                newCookieChoice.setCookieConsent(consent);
                existingChoices.add(newCookieChoice);
            }
        }

        // Automatically add essential cookies if they are missing
        for (String essentialCookie : essentialCookies) {
            boolean alreadyExists = existingChoices.stream()
                    .anyMatch(choice -> choice.getCookieName().equals(essentialCookie));

            if (!alreadyExists) {
                CookieChoice essentialChoice = new CookieChoice(essentialCookie, true);
                essentialChoice.setCookieConsent(consent);
                existingChoices.add(essentialChoice);
            }
        }

        consent.setCookies(existingChoices);
        cookieConsentRepository.save(consent);

        logger.info("Customized consent for userId={} or sessionId={}", request.getUserId(), request.getSessionId());

        return CookieConsentDTO.fromEntity(consent);
    }
    /**
     * Retrieves or creates a cookie consent entry for a user or session.
     *
     * @param userId             The user ID (optional).
     * @param sessionId          The session ID (optional).
     * @param cookiePolicyVersion The cookie policy version.
     * @param cookies            The initial cookie choices.
     * @return The retrieved or newly created CookieConsent entity.
     */
    public CookieConsent getOrCreateConsent(Long userId, String sessionId, CookiePolicyVersion cookiePolicyVersion, List<CookieChoice> cookies) {
        CookieConsent consent = null;

        if (userId != null) {
            consent = cookieConsentRepository.findByUserId(userId).orElse(null);
        }

        if (consent == null && sessionId != null) {
            consent = cookieConsentRepository.findBySessionId(sessionId).orElse(null);
        }

        if (consent == null) {
            if (cookiePolicyVersion == null) {
                throw new  CookieConsentException("The version of the cookie policy is mandatory");
            }

            consent = (userId != null)
                    ? new CookieConsent(userId, cookiePolicyVersion, new ArrayList<>())
                    : new CookieConsent(sessionId, cookiePolicyVersion, new ArrayList<>());

            cookieConsentRepository.save(consent);
        }

        return consent;
    }
}
