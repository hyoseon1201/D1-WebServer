package com.d1.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCharacterRequest(
        @NotBlank @Size(min = 2, max = 50) String name,
        @NotBlank String classType
) {}
