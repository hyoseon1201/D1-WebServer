package com.d1.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "character_quick_slots")
@Getter
@NoArgsConstructor
public class CharacterQuickSlot {

    @EmbeddedId
    private CharacterQuickSlotId id;

    @Column(name = "item_asset_id", nullable = false, length = 100)
    private String itemAssetId;

    public CharacterQuickSlot(Long characterId, int slotKey, String itemAssetId) {
        this.id = new CharacterQuickSlotId(characterId, slotKey);
        this.itemAssetId = itemAssetId;
    }

    public void updateItem(String itemAssetId) {
        this.itemAssetId = itemAssetId;
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    public static class CharacterQuickSlotId implements java.io.Serializable {
        @Column(name = "character_id")
        private Long characterId;

        @Column(name = "slot_key")
        private int slotKey;

        public CharacterQuickSlotId(Long characterId, int slotKey) {
            this.characterId = characterId;
            this.slotKey = slotKey;
        }
    }
}
