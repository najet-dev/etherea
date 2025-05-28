package com.etherea.repositories;

import com.etherea.models.CookieConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CookieConsentRepository extends JpaRepository<CookieConsent, Long> {
    Optional<CookieConsent> findByUserId(Long userId);
    Optional<CookieConsent> findBySessionId(String sessionId);
}
