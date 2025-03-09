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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public CookieConsentDTO getConsentForUser(Long userId) {
        return cookieConsentRepository.findByUserId(userId)
                .map(CookieConsentDTO::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Consent not found for user ID: " + userId));
    }
    /**
     * Récupère la configuration des cookies (essentiels et non-essentiels).
     * @return Map contenant les cookies essentiels et non-essentiels
     */
    public Map<String, List<String>> getCookiesConfig() {
        Map<String, List<String>> cookiesConfig = new HashMap<>();
        cookiesConfig.put("essential", essentialCookies);
        cookiesConfig.put("non-essential", nonEssentialCookies);
        return cookiesConfig;
    }
    @Transactional
    public CookieConsentDTO acceptAllCookies(SaveCookieConsentRequestDTO request) {
        CookieConsent existingConsent = cookieConsentRepository.findByUserId(request.getUserId())
                .orElse(new CookieConsent());

        existingConsent.setUserId(request.getUserId());
        existingConsent.setCookiePolicyVersion(request.getPolicyVersion());
        existingConsent.setConsentDate(LocalDateTime.now());

        List<String> allCookies = Stream.concat(essentialCookies.stream(), nonEssentialCookies.stream())
                .toList();

        if (existingConsent.getCookies() == null) {
            existingConsent.setCookies(new ArrayList<>());
        }

        List<CookieChoice> updatedChoices = allCookies.stream()
                .map(cookieName -> existingConsent.getCookies().stream()
                        .filter(choice -> choice.getCookieName().equals(cookieName))
                        .findFirst()
                        .map(choice -> {
                            choice.setAccepted(true);
                            return choice;
                        })
                        .orElseGet(() -> new CookieChoice(cookieName, true)))
                .collect(Collectors.toList());

        updatedChoices.forEach(choice -> choice.setCookieConsent(existingConsent));
        existingConsent.setCookies(updatedChoices);

        cookieConsentRepository.save(existingConsent);
        logger.info("User {} accepted all cookies", request.getUserId());

        return CookieConsentDTO.fromEntity(existingConsent);
    }
    @Transactional
    public CookieConsentDTO rejectAllCookies(SaveCookieConsentRequestDTO request) {
        CookieConsent existingConsent = cookieConsentRepository.findByUserId(request.getUserId())
                .orElse(new CookieConsent());

        existingConsent.setUserId(request.getUserId());
        existingConsent.setCookiePolicyVersion(request.getPolicyVersion());
        existingConsent.setConsentDate(LocalDateTime.now());

        List<String> allCookies = Stream.concat(essentialCookies.stream(), nonEssentialCookies.stream()).toList();

        List<CookieChoice> updatedChoices = allCookies.stream()
                .map(cookieName -> new CookieChoice(cookieName, essentialCookies.contains(cookieName)))
                .collect(Collectors.toList());

        updatedChoices.forEach(choice -> choice.setCookieConsent(existingConsent));
        existingConsent.setCookies(updatedChoices);

        cookieConsentRepository.save(existingConsent);
        logger.info("User {} rejected all cookies except essential ones", request.getUserId());

        return CookieConsentDTO.fromEntity(existingConsent);
    }

    @Transactional
    public CookieConsentDTO customizeCookies(SaveCookieConsentRequestDTO request) {
        CookieConsent existingConsent = cookieConsentRepository.findByUserId(request.getUserId())
                .orElse(new CookieConsent());

        existingConsent.setUserId(request.getUserId());
        existingConsent.setCookiePolicyVersion(request.getPolicyVersion());
        existingConsent.setConsentDate(LocalDateTime.now());

        if (request.getCookieChoices() == null) {
            throw new IllegalArgumentException("Cookie choices must not be null");
        }

        if (existingConsent.getCookies() == null) {
            existingConsent.setCookies(new ArrayList<>());
        }

        List<CookieChoice> existingChoices = existingConsent.getCookies();

        for (CookieChoiceDTO newChoice : request.getCookieChoices()) {
            if (!essentialCookies.contains(newChoice.getCookieName()) && !nonEssentialCookies.contains(newChoice.getCookieName())) {
                throw new IllegalArgumentException("Invalid cookie name: " + newChoice.getCookieName());
            }

            CookieChoice cookieChoice = existingChoices.stream()
                    .filter(choice -> choice.getCookieName().equals(newChoice.getCookieName()))
                    .findFirst()
                    .orElse(null);

            if (cookieChoice != null) {
                cookieChoice.setAccepted(newChoice.isAccepted());
            } else {
                CookieChoice newCookieChoice = new CookieChoice(newChoice.getCookieName(), newChoice.isAccepted());
                newCookieChoice.setCookieConsent(existingConsent);
                existingChoices.add(newCookieChoice);
            }
        }

        cookieConsentRepository.save(existingConsent);
        logger.info("User {} customized cookies", request.getUserId());

        return CookieConsentDTO.fromEntity(existingConsent);
    }
}
