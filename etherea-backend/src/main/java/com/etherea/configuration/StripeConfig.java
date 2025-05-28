package com.etherea.configuration;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    /**
     * Constructs the StripeConfig and sets the Stripe secret API key.
     *
     * @param apiKey the secret API key for Stripe, loaded from application properties
     */
    public StripeConfig(@Value("${stripe.secret.key}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    /**
     * The Stripe webhook secret used to validate incoming webhook events.
     */
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    /**
     * Retrieves the configured Stripe webhook secret.
     *
     * @return the webhook secret string
     */
    public String getWebhookSecret() {
        return webhookSecret;
    }
}
