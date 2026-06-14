package com.d1.server.dto;

import com.d1.server.entity.CharacterStats;

public record StatsDto(
        int level,
        int xp,
        int attributePoints,
        int skillPoints,
        int strength,
        int intelligence,
        int dexterity,
        int luck
) {
    public static StatsDto from(CharacterStats stats) {
        return new StatsDto(
                stats.getLevel(), stats.getXp(),
                stats.getAttributePoints(), stats.getSkillPoints(),
                stats.getStrength(), stats.getIntelligence(),
                stats.getDexterity(), stats.getLuck()
        );
    }
}
