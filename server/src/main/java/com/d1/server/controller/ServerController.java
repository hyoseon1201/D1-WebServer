package com.d1.server.controller;

import com.d1.server.dto.CharacterDataResponse;
import com.d1.server.dto.SaveCharacterRequest;
import com.d1.server.dto.VerifySessionRequest;
import com.d1.server.service.GameDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 데디서버 전용 엔드포인트 — X-Server-Api-Key 인증 (JwtInterceptor 적용 안 됨)
@RestController
@RequestMapping("/api/server")
@RequiredArgsConstructor
public class ServerController {

    private final GameDataService gameDataService;

    // 세션 검증 + 캐릭터 데이터 로드. sessionToken은 현재 미검증(MVP) — 추후 세션 테이블로 교체.
    @PostMapping("/characters/{characterId}/verify-session")
    public ResponseEntity<CharacterDataResponse> verifySession(
            @PathVariable Long characterId,
            @Valid @RequestBody VerifySessionRequest request) {
        return ResponseEntity.ok(gameDataService.loadCharacterData(characterId));
    }

    @PostMapping("/characters/{characterId}/save")
    public ResponseEntity<Void> saveCharacter(
            @PathVariable Long characterId,
            @Valid @RequestBody SaveCharacterRequest request) {
        gameDataService.saveCharacterData(characterId, request);
        return ResponseEntity.noContent().build();
    }
}
