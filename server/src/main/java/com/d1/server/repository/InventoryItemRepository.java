package com.d1.server.repository;

import com.d1.server.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findByCharacterId(Long characterId);

    void deleteByCharacterId(Long characterId);
}
