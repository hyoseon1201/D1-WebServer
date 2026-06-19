package com.d1.server.service;

import com.d1.server.dto.CharacterResponse;
import com.d1.server.dto.CreateCharacterRequest;
import com.d1.server.entity.GameCharacter;
import com.d1.server.exception.ApiException;
import com.d1.server.repository.CharacterStatsRepository;
import com.d1.server.repository.GameCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private final GameCharacterRepository characterRepository;
    private final CharacterStatsRepository statsRepository;

    @Transactional(readOnly = true)
    public List<CharacterResponse> getCharacters(Long accountId) {
        return characterRepository.findByAccountId(accountId).stream()
                .map(character -> {
                    int level = statsRepository.findById(character.getCharacterId())
                            .map(stats -> stats.getLevel())
                            .orElse(1);
                    return CharacterResponse.from(character, level);
                })
                .toList();
    }

    private static final int MAX_CHARACTERS_PER_ACCOUNT = 4;

    @Transactional
    public CharacterResponse createCharacter(Long accountId, CreateCharacterRequest request) {
        if (characterRepository.findByAccountId(accountId).size() >= MAX_CHARACTERS_PER_ACCOUNT) {
            throw new ApiException(HttpStatus.CONFLICT, "캐릭터는 최대 4개까지 생성할 수 있습니다.");
        }
        if (characterRepository.existsByName(request.name())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 캐릭터 이름입니다.");
        }
        GameCharacter character = new GameCharacter(accountId, request.name(), request.classType());
        characterRepository.save(character);
        return CharacterResponse.from(character, 1);
        // character_stats는 생성하지 않음 — 첫 접속 시 언리얼이 ScalableFloat 초기값으로 세팅 후 save API로 저장
    }
}
