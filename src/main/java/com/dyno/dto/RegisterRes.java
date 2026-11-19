package com.dyno.dto;

public record RegisterRes(
        String message,
        String username,
        String recoveryPhrase
) {}