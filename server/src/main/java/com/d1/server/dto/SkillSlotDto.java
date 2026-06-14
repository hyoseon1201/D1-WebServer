package com.d1.server.dto;

import com.d1.server.entity.CharacterSkillSlot;

public record SkillSlotDto(String slotKey, String skillTag) {
    public static SkillSlotDto from(CharacterSkillSlot slot) {
        return new SkillSlotDto(slot.getId().getSlotKey(), slot.getSkillTag());
    }
}
