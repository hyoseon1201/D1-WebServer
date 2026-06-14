package com.d1.server.dto;

import com.d1.server.entity.InventoryItem;

public record InventoryItemDto(int slotIndex, String itemAssetId, int quantity) {
    public static InventoryItemDto from(InventoryItem item) {
        return new InventoryItemDto(item.getSlotIndex(), item.getItemAssetId(), item.getQuantity());
    }
}
