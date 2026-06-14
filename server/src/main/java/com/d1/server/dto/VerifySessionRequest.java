package com.d1.server.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifySessionRequest(@NotBlank String sessionToken) {}
