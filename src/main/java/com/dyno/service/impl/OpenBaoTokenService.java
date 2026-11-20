package com.dyno.service.impl;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.dyno.config.OpenBaoProperties;

@Service
public class OpenBaoTokenService {

    private final RestClient restClient;
    private final OpenBaoProperties properties;

    private volatile String clientToken;
    private volatile Instant expiresAt;

    public OpenBaoTokenService(OpenBaoProperties properties) {

        this.properties = properties;

        this.restClient = RestClient.builder()
                .baseUrl(properties.getAddress())
                .build();
    }

    public synchronized String getToken() {

        if (clientToken != null
                && expiresAt != null
                && Instant.now().isBefore(expiresAt)) {

            return clientToken;
        }

        return authenticate();
    }

    private String authenticate() {

        Map<String, String> request = Map.of(
                "role_id", properties.getRoleId(),
                "secret_id", properties.getSecretId()
        );

        Map<?, ?> response = restClient.post()
                .uri("/v1/auth/approle/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null || !(response.get("auth") instanceof Map<?, ?> auth)) {
            throw new IllegalStateException(
                    "Invalid OpenBao authentication response"
            );
        }

        Object token = auth.get("client_token");
        Object lease = auth.get("lease_duration");

        if (!(token instanceof String newToken)
                || !(lease instanceof Number duration)) {

            throw new IllegalStateException(
                    "OpenBao did not return a valid client token"
            );
        }

        long leaseSeconds = duration.longValue();

        if (leaseSeconds <= 0) {
            throw new IllegalStateException(
                    "OpenBao returned an invalid token lease duration"
            );
        }

        this.clientToken = newToken;

        long safetyWindow = Math.min(60, leaseSeconds / 10);

        this.expiresAt = Instant.now().plusSeconds(
                leaseSeconds - safetyWindow
        );

        return clientToken;
    }
}