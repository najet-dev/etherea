package com.etherea.configuration;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    public StripeConfig(@Value("${stripe.secret.key}") String apiKey) {
        Stripe.apiKey = apiKey;
    }
    // Charger la clé secrète du webhook
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    // Méthode pour récupérer la clé secrète du webhook
    public String getWebhookSecret() {
        return webhookSecret;
    }
}
