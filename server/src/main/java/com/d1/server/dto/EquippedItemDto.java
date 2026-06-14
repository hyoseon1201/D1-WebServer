package com.d1.server.dto;

import com.d1.server.entity.EquippedItem;

public record EquippedItemDto(String slotType, String itemAssetId) {
    public static EquippedItemDto from(EquippedItem item) {
        return new EquippedItemDto(item.getId().getSlotType(), item.getItemAssetId());
    }
}
