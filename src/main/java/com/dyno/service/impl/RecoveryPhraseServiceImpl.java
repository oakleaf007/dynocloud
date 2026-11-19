package com.dyno.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.dyno.service.RecoveryPhraseService;

@Service
public class RecoveryPhraseServiceImpl implements RecoveryPhraseService {

    private final SecureRandom secureRandom = new SecureRandom();

    private final List<String> words;

    public RecoveryPhraseServiceImpl() throws IOException {

        try (var stream = getClass()
                .getResourceAsStream("/bip39-english.txt")) {

            if (stream == null) {
                throw new IllegalStateException("Wordlist not found");
            }

            try (var reader = new BufferedReader(
                    new InputStreamReader(stream))) {

                words = reader.lines().toList();
            }
        }
    }

    public String generatePhrase() {

        List<String> phrase = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            int index = secureRandom.nextInt(words.size());
            phrase.add(words.get(index));
        }

        return String.join(" ", phrase);
    }
    
    public String normalize(String phrase) {

        if (phrase == null || phrase.isBlank()) {
            throw new IllegalArgumentException(
                "Recovery phrase cannot be empty"
            );
        }

        return phrase.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
    
    public String toSha256Base64(String normalizedPhrase) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(normalizedPhrase.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes); // Always 44 bytes
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}