package com.etherea.repositories;

import com.etherea.models.Newsletter;
import com.etherea.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
    Optional<Newsletter> findByEmail(String email);}
