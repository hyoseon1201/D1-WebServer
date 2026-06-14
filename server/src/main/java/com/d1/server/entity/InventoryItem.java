package com.d1.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"character_id", "slot_index"}))
@Getter
@NoArgsConstructor
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "character_id", nullable = false)
    private Long characterId;

    @Column(name = "slot_index", nullable = false)
    private int slotIndex;

    @Column(name = "item_asset_id", nullable = false, length = 100)
    private String itemAssetId;

    @Column(nullable = false)
    private int quantity = 1;

    public InventoryItem(Long characterId, int slotIndex, String itemAssetId, int quantity) {
        this.characterId = characterId;
        this.slotIndex = slotIndex;
        this.itemAssetId = itemAssetId;
        this.quantity = quantity;
    }

    public void update(String itemAssetId, int quantity) {
        this.itemAssetId = itemAssetId;
        this.quantity = quantity;
    }
}
