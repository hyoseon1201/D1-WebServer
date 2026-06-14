package com.d1.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipped_items")
@Getter
@NoArgsConstructor
public class EquippedItem {

    @EmbeddedId
    private EquippedItemId id;

    @Column(name = "item_asset_id", nullable = false, length = 100)
    private String itemAssetId;

    public EquippedItem(Long characterId, String slotType, String itemAssetId) {
        this.id = new EquippedItemId(characterId, slotType);
        this.itemAssetId = itemAssetId;
    }

    public void updateItem(String itemAssetId) {
        this.itemAssetId = itemAssetId;
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    public static class EquippedItemId implements java.io.Serializable {
        @Column(name = "character_id")
        private Long characterId;

        @Column(name = "slot_type", length = 20)
        private String slotType;

        public EquippedItemId(Long characterId, String slotType) {
            this.characterId = characterId;
            this.slotType = slotType;
        }
    }
}
