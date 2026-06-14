package com.d1.server.dto;

import java.util.List;

// verify-session 응답 — stats가 null이면 신규 캐릭터 (언리얼이 ScalableFloat 초기값으로 세팅 후 save 호출)
public record CharacterDataResponse(
        Long characterId,
        String name,
        String classType,
        StatsDto stats,
        List<SkillDto> skills,
        List<SkillSlotDto> skillSlots,
        List<InventoryItemDto> inventory,
        List<EquippedItemDto> equippedItems,
        List<QuickSlotDto> quickSlots
) {}
