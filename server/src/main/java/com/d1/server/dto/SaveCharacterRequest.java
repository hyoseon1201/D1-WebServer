package com.d1.server.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaveCharacterRequest(
        @NotNull @Valid StatsDto stats,
        @NotNull List<@Valid SkillDto> skills,
        @NotNull List<@Valid SkillSlotDto> skillSlots,
        @NotNull List<@Valid InventoryItemDto> inventory,
        @NotNull List<@Valid EquippedItemDto> equippedItems,
        @NotNull List<@Valid QuickSlotDto> quickSlots
) {}
