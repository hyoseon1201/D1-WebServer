package com.d1.server.dto;

import com.d1.server.entity.GameCharacter;

import java.time.LocalDateTime;

public record CharacterResponse(
        Long characterId,
        String name,
        String classType,
        LocalDateTime createdAt
) {
    public static CharacterResponse from(GameCharacter character) {
        return new CharacterResponse(
                character.getCharacterId(),
                character.getName(),
                character.getClassType(),
                character.getCreatedAt()
        );
    }
}
