package com.etherea.services;

import com.etherea.dtos.CookieChoiceDTO;
import com.etherea.dtos.CookieConsentDTO;
import com.etherea.dtos.SaveCookieConsentRequestDTO;
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
import java.util.stream.Collectors;
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
    public Map<String, List<String>> getCookiesConfig() {
        Map<String, List<String>> cookiesConfig = new HashMap<>();
        cookiesConfig.put("essential", essentialCookies);
        cookiesConfig.put("non-essential", nonEssentialCookies);
        return cookiesConfig;
    }

    @Transactional
    public CookieConsentDTO acceptAllCookies(SaveCookieConsentRequestDTO request) {
        if (request.getCookiePolicyVersion() == null || request.getCookiePolicyVersion().isEmpty()) {
            throw new IllegalArgumentException("La version de la politique de cookies doit être spécifiée.");
        }

        CookieConsent consent = getOrCreateConsent(request.getUserId(), request.getSessionId(), request.getCookiePolicyVersion(), new ArrayList<>());

        consent.setCookiePolicyVersion(request.getCookiePolicyVersion());
        consent.setConsentDate(LocalDateTime.now());

        List<String> allCookies = Stream.concat(essentialCookies.stream(), nonEssentialCookies.stream()).toList();
        List<CookieChoice> updatedChoices = allCookies.stream()
                .map(cookieName -> new CookieChoice(cookieName, true, consent))
                .collect(Collectors.toList());

        consent.setCookies(updatedChoices);
        cookieConsentRepository.save(consent);

        logger.info("Consentement accepté pour userId={} ou sessionId={}", request.getUserId(), request.getSessionId());

        return CookieConsentDTO.fromEntity(consent);
    }

    @Transactional
    public CookieConsentDTO rejectAllCookies(SaveCookieConsentRequestDTO request) {
        if (request.getCookiePolicyVersion() == null || request.getCookiePolicyVersion().isEmpty()) {
            throw new IllegalArgumentException("La version de la politique de cookies doit être spécifiée.");
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

        logger.info("Consentement rejeté pour userId={} ou sessionId={}", request.getUserId(), request.getSessionId());

        return CookieConsentDTO.fromEntity(consent);
    }

    @Transactional
    public CookieConsentDTO customizeCookies(SaveCookieConsentRequestDTO request) {
        if (request.getCookiePolicyVersion() == null || request.getCookiePolicyVersion().isEmpty()) {
            throw new IllegalArgumentException("La version de la politique de cookies doit être spécifiée.");
        }

        if (request.getCookieChoices() == null || request.getCookieChoices().isEmpty()) {
            throw new IllegalArgumentException("Les choix de cookies ne doivent pas être null ou vides.");
        }

        CookieConsent consent = getOrCreateConsent(request.getUserId(), request.getSessionId(), request.getCookiePolicyVersion(), new ArrayList<>());
        consent.setCookiePolicyVersion(request.getCookiePolicyVersion());
        consent.setConsentDate(LocalDateTime.now());

        List<CookieChoice> existingChoices = consent.getCookies() != null ? new ArrayList<>(consent.getCookies()) : new ArrayList<>();

        for (CookieChoiceDTO newChoice : request.getCookieChoices()) {
            if (!essentialCookies.contains(newChoice.getCookieName()) && !nonEssentialCookies.contains(newChoice.getCookieName())) {
                throw new IllegalArgumentException("Nom de cookie invalide : " + newChoice.getCookieName());
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

        consent.setCookies(existingChoices);
        cookieConsentRepository.save(consent);

        logger.info("Consentement personnalisé pour userId={} ou sessionId={}", request.getUserId(), request.getSessionId());

        return CookieConsentDTO.fromEntity(consent);
    }

    public CookieConsent getOrCreateConsent(Long userId, String sessionId, String cookiePolicyVersion, List<CookieChoice> cookies) {
        CookieConsent consent = null;

        if (userId != null) {
            consent = cookieConsentRepository.findByUserId(userId).orElse(null);
        }

        if (consent == null && sessionId != null) {
            consent = cookieConsentRepository.findBySessionId(sessionId).orElse(null);
        }

        if (consent == null) {
            if (cookiePolicyVersion == null || cookiePolicyVersion.isEmpty()) {
                throw new IllegalArgumentException("La version de la politique de cookies est obligatoire.");
            }

            consent = (userId != null)
                    ? new CookieConsent(userId, cookiePolicyVersion, new ArrayList<>())
                    : new CookieConsent(sessionId, cookiePolicyVersion, new ArrayList<>());

            cookieConsentRepository.save(consent);
        }

        return consent;
    }
}
