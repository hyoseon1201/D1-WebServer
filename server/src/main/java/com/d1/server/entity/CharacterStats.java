package com.d1.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "character_stats")
@Getter
@NoArgsConstructor
public class CharacterStats {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(nullable = false)
    private int level = 1;

    @Column(nullable = false)
    private int xp = 0;

    @Column(name = "attribute_points", nullable = false)
    private int attributePoints = 0;

    @Column(name = "skill_points", nullable = false)
    private int skillPoints = 0;

    @Column(nullable = false)
    private int strength = 0;

    @Column(nullable = false)
    private int intelligence = 0;

    @Column(nullable = false)
    private int dexterity = 0;

    @Column(nullable = false)
    private int luck = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public CharacterStats(Long characterId) {
        this.characterId = characterId;
    }

    public void update(int level, int xp, int attributePoints, int skillPoints,
                       int strength, int intelligence, int dexterity, int luck) {
        this.level = level;
        this.xp = xp;
        this.attributePoints = attributePoints;
        this.skillPoints = skillPoints;
        this.strength = strength;
        this.intelligence = intelligence;
        this.dexterity = dexterity;
        this.luck = luck;
    }
}
