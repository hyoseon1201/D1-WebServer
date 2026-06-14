package com.d1.server.service;

import com.d1.server.dto.CharacterResponse;
import com.d1.server.dto.CreateCharacterRequest;
import com.d1.server.entity.GameCharacter;
import com.d1.server.exception.ApiException;
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

    @Transactional(readOnly = true)
    public List<CharacterResponse> getCharacters(Long accountId) {
        return characterRepository.findByAccountId(accountId).stream()
                .map(CharacterResponse::from)
                .toList();
    }

    @Transactional
    public CharacterResponse createCharacter(Long accountId, CreateCharacterRequest request) {
        if (characterRepository.existsByName(request.name())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 캐릭터 이름입니다.");
        }
        GameCharacter character = new GameCharacter(accountId, request.name(), request.classType());
        characterRepository.save(character);
        return CharacterResponse.from(character);
        // character_stats는 생성하지 않음 — 첫 접속 시 언리얼이 ScalableFloat 초기값으로 세팅 후 save API로 저장
    }
}
