package com.d1.server.dto;

import com.d1.server.entity.CharacterSkill;

public record SkillDto(String skillTag, int skillLevel) {
    public static SkillDto from(CharacterSkill skill) {
        return new SkillDto(skill.getSkillTag(), skill.getSkillLevel());
    }
}
