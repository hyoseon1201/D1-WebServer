package com.d1.server.repository;

import com.d1.server.entity.EquippedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquippedItemRepository
        extends JpaRepository<EquippedItem, EquippedItem.EquippedItemId> {

    List<EquippedItem> findById_CharacterId(Long characterId);

    void deleteById_CharacterId(Long characterId);
}
