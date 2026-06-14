package com.d1.server.repository;

import com.d1.server.entity.CharacterQuickSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterQuickSlotRepository
        extends JpaRepository<CharacterQuickSlot, CharacterQuickSlot.CharacterQuickSlotId> {

    List<CharacterQuickSlot> findById_CharacterId(Long characterId);

    void deleteById_CharacterId(Long characterId);
}
