package com.dyno.deaddrop.dto;

import java.time.Instant;

public record DeadDropUploadResponse(

        String downloadUrl,

        String deletionSecret,

        Instant createdAt,

        Instant expiresAt

) {}