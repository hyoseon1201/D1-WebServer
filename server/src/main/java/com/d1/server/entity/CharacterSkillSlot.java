package com.d1.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "character_skill_slots")
@Getter
@NoArgsConstructor
public class CharacterSkillSlot {

    @EmbeddedId
    private CharacterSkillSlotId id;

    @Column(name = "skill_tag", nullable = false, length = 100)
    private String skillTag;

    public CharacterSkillSlot(Long characterId, String slotKey, String skillTag) {
        this.id = new CharacterSkillSlotId(characterId, slotKey);
        this.skillTag = skillTag;
    }

    public void updateSkillTag(String skillTag) {
        this.skillTag = skillTag;
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    public static class CharacterSkillSlotId implements java.io.Serializable {
        @Column(name = "character_id")
        private Long characterId;

        @Column(name = "slot_key", length = 1)
        private String slotKey;

        public CharacterSkillSlotId(Long characterId, String slotKey) {
            this.characterId = characterId;
            this.slotKey = slotKey;
        }
    }
}
