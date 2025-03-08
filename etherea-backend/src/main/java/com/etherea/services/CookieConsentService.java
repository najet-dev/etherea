package com.etherea.services;

import com.etherea.dtos.CookieChoiceDTO;
import com.etherea.dtos.CookieConsentDTO;
import com.etherea.dtos.SaveCookieConsentRequestDTO;
import com.etherea.models.CookieChoice;
import com.etherea.models.CookieConsent;
import com.etherea.repositories.CookieConsentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class CookieConsentService {
    private final CookieConsentRepository cookieConsentRepository;
    public CookieConsentService(CookieConsentRepository cookieConsentRepository) {
        this.cookieConsentRepository = cookieConsentRepository;
    }

    /**
     * Accepts all cookies (including non-essential ones).
     *
     * @param request Contains user ID and cookie policy version.
     * @return CookieConsentDTO representing the current consent status.
     */
    @Transactional
    public CookieConsentDTO acceptAllCookies(SaveCookieConsentRequestDTO request) {
        CookieConsent existingConsent = cookieConsentRepository.findByUserId(request.getUserId())
                .orElse(new CookieConsent());

        existingConsent.setUserId(request.getUserId());
        existingConsent.setCookiePolicyVersion(request.getPolicyVersion());
        existingConsent.setConsentDate(LocalDateTime.now());

        // Pre-defined list of cookies to accept
        List<String> allCookies = Stream.concat(ESSENTIAL_COOKIES.stream(), NON_ESSENTIAL_COOKIES.stream())
                .toList();

        // Retrieve current choices from existing consent
        List<CookieChoice> currentChoices = existingConsent.getCookies();

        // Update existing cookies or add new ones
        List<CookieChoice> updatedChoices = allCookies.stream()
                .map(cookieName -> {
                    // Check if the cookie already exists in current choices
                    CookieChoice existingChoice = currentChoices.stream()
                            .filter(choice -> choice.getCookieName().equals(cookieName))
                            .findFirst()
                            .orElse(null);

                    if (existingChoice != null) {
                        // If the cookie exists, update its acceptance status
                        existingChoice.setAccepted(true);
                        return existingChoice;
                    } else {
                        // If the cookie doesn't exist yet, create it
                        return new CookieChoice(cookieName, true);
                    }
                })
                .collect(Collectors.toList());

        // Associate cookies with the existing consent
        updatedChoices.forEach(choice -> choice.setCookieConsent(existingConsent));
        existingConsent.setCookies(updatedChoices);

        cookieConsentRepository.save(existingConsent);

        return CookieConsentDTO.fromEntity(existingConsent);
    }

    /**
     * Rejects all cookies except essential ones.
     *
     * @param request Contains user ID and cookie policy version.
     * @return CookieConsentDTO representing the current consent status.
     */
    @Transactional
    public CookieConsentDTO rejectAllCookies(SaveCookieConsentRequestDTO request) {
        CookieConsent existingConsent = cookieConsentRepository.findByUserId(request.getUserId())
                .orElse(new CookieConsent());

        existingConsent.setUserId(request.getUserId());
        existingConsent.setCookiePolicyVersion(request.getPolicyVersion());
        existingConsent.setConsentDate(LocalDateTime.now());

        // Reject all cookies except essential ones
        existingConsent.getCookies().forEach(choice -> {
            if (!ESSENTIAL_COOKIES.contains(choice.getCookieName())) {
                choice.setAccepted(false);
            }
        });

        cookieConsentRepository.save(existingConsent);

        return CookieConsentDTO.fromEntity(existingConsent);
    }

    /**
     * Allows the user to customize their cookie choices.
     *
     * @param request Contains user ID, cookie policy version, and custom cookie choices.
     * @return CookieConsentDTO representing the current consent status.
     */
    @Transactional
    public CookieConsentDTO customizeCookies(SaveCookieConsentRequestDTO request) {
        CookieConsent existingConsent = cookieConsentRepository.findByUserId(request.getUserId())
                .orElse(new CookieConsent());

        existingConsent.setUserId(request.getUserId());
        existingConsent.setCookiePolicyVersion(request.getPolicyVersion());

        List<CookieChoice> cookies = new ArrayList<>(existingConsent.getCookies());

        // Update user's custom cookie choices
        for (CookieChoiceDTO newChoice : request.getCookieChoices()) {
            // Find corresponding cookie in the existing list
            CookieChoice existingChoice = cookies.stream()
                    .filter(choice -> choice.getCookieName().equals(newChoice.getCookieName()))
                    .findFirst()
                    .orElse(null); // If the cookie doesn't exist yet, create it

            if (existingChoice != null) {
                // If the cookie exists, update its acceptance status
                existingChoice.setAccepted(newChoice.isAccepted());
            } else {
                // If the cookie doesn't exist, create it
                CookieChoice newCookieChoice = new CookieChoice(newChoice.getCookieName(), newChoice.isAccepted());
                newCookieChoice.setCookieConsent(existingConsent);
                cookies.add(newCookieChoice);
            }
        }

        // Update the list of cookies in the consent
        existingConsent.setCookies(cookies);

        existingConsent.setConsentDate(LocalDateTime.now());

        cookieConsentRepository.save(existingConsent);

        return CookieConsentDTO.fromEntity(existingConsent);
    }
    private static final List<String> ESSENTIAL_COOKIES = List.of(
            "JSESSIONID", "cart_items", "cart_id", "user_session", "auth_token", "XSRF-TOKEN", "currency", "language"
    );
    private static final List<String> NON_ESSENTIAL_COOKIES = List.of(
            "GOOGLE_ANALYTICS", "FACEBOOK_PIXEL", "TIKTOK_PIXEL"
    );
}
