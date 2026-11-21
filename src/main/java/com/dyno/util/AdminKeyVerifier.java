package com.dyno.util;



import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminKeyVerifier {

    private final String adminKey;

    public AdminKeyVerifier(
            @Value("${dynocloud.admin.key}") String adminKey) {
        this.adminKey = adminKey;
    }

    public boolean verifyAdminKey(String submittedKey) {

        if (submittedKey == null || submittedKey.isBlank()) {
            return false;
        }

        if (adminKey == null || adminKey.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
            adminKey.getBytes(StandardCharsets.UTF_8),
            submittedKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}


