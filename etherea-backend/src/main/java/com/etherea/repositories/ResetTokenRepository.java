package com.etherea.repositories;

import com.etherea.models.ResetToken;
import com.etherea.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    Optional<ResetToken> findByToken(String token);

    Optional<ResetToken> findByUserId(Long userId);
    void deleteByUser(User user);}
