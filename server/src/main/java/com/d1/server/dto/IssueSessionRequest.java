package com.d1.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IssueSessionRequest(
        @NotNull Long characterId,
        @NotBlank String destination   // "town" | "dungeon"
) {}
