package com.d1.server.dto;

import com.d1.server.entity.CharacterQuickSlot;

public record QuickSlotDto(int slotKey, String itemAssetId) {
    public static QuickSlotDto from(CharacterQuickSlot slot) {
        return new QuickSlotDto(slot.getId().getSlotKey(), slot.getItemAssetId());
    }
}
