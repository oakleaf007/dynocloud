package com.dyno.service;

public interface RecoveryPhraseService {
public String generatePhrase();

public String toSha256Base64(String normalizedPhrase);
public String normalize(String pharse);
}
