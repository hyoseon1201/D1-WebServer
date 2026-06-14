package com.d1.server.repository;

import com.d1.server.entity.CharacterSkillSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterSkillSlotRepository
        extends JpaRepository<CharacterSkillSlot, CharacterSkillSlot.CharacterSkillSlotId> {

    List<CharacterSkillSlot> findById_CharacterId(Long characterId);

    void deleteById_CharacterId(Long characterId);
}
