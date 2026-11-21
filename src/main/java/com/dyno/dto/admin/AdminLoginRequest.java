package com.dyno.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String adminKey
) {}

