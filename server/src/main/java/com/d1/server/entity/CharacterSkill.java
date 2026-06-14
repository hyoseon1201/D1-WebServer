package com.d1.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "character_skills",
        uniqueConstraints = @UniqueConstraint(columnNames = {"character_id", "skill_tag"}))
@Getter
@NoArgsConstructor
public class CharacterSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "character_id", nullable = false)
    private Long characterId;

    @Column(name = "skill_tag", nullable = false, length = 100)
    private String skillTag;

    @Column(name = "skill_level", nullable = false)
    private int skillLevel = 1;

    public CharacterSkill(Long characterId, String skillTag, int skillLevel) {
        this.characterId = characterId;
        this.skillTag = skillTag;
        this.skillLevel = skillLevel;
    }

    public void updateLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }
}
