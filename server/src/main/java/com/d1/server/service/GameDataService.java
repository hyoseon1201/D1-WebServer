package com.d1.server.service;

import com.d1.server.dto.*;
import com.d1.server.entity.*;
import com.d1.server.exception.ApiException;
import com.d1.server.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameDataService {

    private final GameCharacterRepository characterRepository;
    private final CharacterStatsRepository statsRepository;
    private final CharacterSkillRepository skillRepository;
    private final CharacterSkillSlotRepository skillSlotRepository;
    private final InventoryItemRepository inventoryRepository;
    private final EquippedItemRepository equippedItemRepository;
    private final CharacterQuickSlotRepository quickSlotRepository;

    @Transactional(readOnly = true)
    public CharacterDataResponse loadCharacterData(Long characterId) {
        GameCharacter character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "캐릭터를 찾을 수 없습니다."));

        // stats가 없으면 null 반환 — 언리얼이 ScalableFloat 초기값으로 세팅 후 save 호출
        StatsDto stats = statsRepository.findById(characterId)
                .map(StatsDto::from)
                .orElse(null);

        List<SkillDto> skills = skillRepository.findByCharacterId(characterId).stream()
                .map(SkillDto::from).toList();

        List<SkillSlotDto> skillSlots = skillSlotRepository.findById_CharacterId(characterId).stream()
                .map(SkillSlotDto::from).toList();

        List<InventoryItemDto> inventory = inventoryRepository.findByCharacterId(characterId).stream()
                .map(InventoryItemDto::from).toList();

        List<EquippedItemDto> equippedItems = equippedItemRepository.findById_CharacterId(characterId).stream()
                .map(EquippedItemDto::from).toList();

        List<QuickSlotDto> quickSlots = quickSlotRepository.findById_CharacterId(characterId).stream()
                .map(QuickSlotDto::from).toList();

        return new CharacterDataResponse(
                character.getCharacterId(), character.getName(), character.getClassType(),
                stats, skills, skillSlots, inventory, equippedItems, quickSlots
        );
    }

    @Transactional
    public void saveCharacterData(Long characterId, SaveCharacterRequest request) {
        if (!characterRepository.existsById(characterId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "캐릭터를 찾을 수 없습니다.");
        }

        saveStats(characterId, request.stats());
        saveSkills(characterId, request.skills());
        saveSkillSlots(characterId, request.skillSlots());
        saveInventory(characterId, request.inventory());
        saveEquippedItems(characterId, request.equippedItems());
        saveQuickSlots(characterId, request.quickSlots());
    }

    private void saveStats(Long characterId, StatsDto dto) {
        CharacterStats stats = statsRepository.findById(characterId)
                .orElseGet(() -> statsRepository.save(new CharacterStats(characterId)));
        stats.update(dto.level(), dto.xp(), dto.attributePoints(), dto.skillPoints(),
                dto.strength(), dto.intelligence(), dto.dexterity(), dto.luck());
    }

    private void saveSkills(Long characterId, List<SkillDto> dtos) {
        // 기존 전체 삭제 후 재삽입 (스킬 목록 변동이 많지 않고 단순함 우선)
        skillRepository.deleteByCharacterId(characterId);
        skillRepository.flush();
        dtos.forEach(dto -> skillRepository.save(
                new CharacterSkill(characterId, dto.skillTag(), dto.skillLevel())));
    }

    private void saveSkillSlots(Long characterId, List<SkillSlotDto> dtos) {
        skillSlotRepository.deleteById_CharacterId(characterId);
        skillSlotRepository.flush();
        dtos.forEach(dto -> skillSlotRepository.save(
                new CharacterSkillSlot(characterId, dto.slotKey(), dto.skillTag())));
    }

    private void saveInventory(Long characterId, List<InventoryItemDto> dtos) {
        inventoryRepository.deleteByCharacterId(characterId);
        inventoryRepository.flush();
        dtos.forEach(dto -> inventoryRepository.save(
                new InventoryItem(characterId, dto.slotIndex(), dto.itemAssetId(), dto.quantity())));
    }

    private void saveEquippedItems(Long characterId, List<EquippedItemDto> dtos) {
        equippedItemRepository.deleteById_CharacterId(characterId);
        equippedItemRepository.flush();
        dtos.forEach(dto -> equippedItemRepository.save(
                new EquippedItem(characterId, dto.slotType(), dto.itemAssetId())));
    }

    private void saveQuickSlots(Long characterId, List<QuickSlotDto> dtos) {
        quickSlotRepository.deleteById_CharacterId(characterId);
        quickSlotRepository.flush();
        dtos.forEach(dto -> quickSlotRepository.save(
                new CharacterQuickSlot(characterId, dto.slotKey(), dto.itemAssetId())));
    }
}
