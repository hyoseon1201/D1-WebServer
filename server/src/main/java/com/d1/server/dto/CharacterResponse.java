package com.d1.server.dto;

import com.d1.server.entity.GameCharacter;

import java.time.LocalDateTime;

public record CharacterResponse(
        Long characterId,
        String name,
        String classType,
        int level,
        LocalDateTime createdAt
) {
    public static CharacterResponse from(GameCharacter character, int level) {
        return new CharacterResponse(
                character.getCharacterId(),
                character.getName(),
                character.getClassType(),
                level,
                character.getCreatedAt()
        );
    }
}
