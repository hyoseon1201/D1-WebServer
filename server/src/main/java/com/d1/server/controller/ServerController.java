package com.d1.server.controller;

import com.d1.server.dto.CharacterDataResponse;
import com.d1.server.dto.IssueSessionRequest;
import com.d1.server.dto.IssueSessionResponse;
import com.d1.server.dto.SaveCharacterRequest;
import com.d1.server.dto.VerifySessionRequest;
import com.d1.server.service.GameDataService;
import com.d1.server.service.MatchmakingService;
import com.d1.server.util.JwtUtil;
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
    private final MatchmakingService matchmakingService;
    private final JwtUtil jwtUtil;

    // 세션 토큰 검증 → characterId 추출 → 캐릭터 데이터 로드.
    // 데디서버는 토큰을 해독하지 않고 그대로 전달만 한다 (secret은 웹서버에만 존재).
    @PostMapping("/sessions/verify")
    public ResponseEntity<CharacterDataResponse> verifySession(
            @Valid @RequestBody VerifySessionRequest request) {
        Long characterId = jwtUtil.validateSessionTokenGetCharacterId(request.sessionToken());
        return ResponseEntity.ok(gameDataService.loadCharacterData(characterId));
    }

    // 서버간 이동용 세션 토큰 발급 — Town→Dungeon 또는 Dungeon→Town ClientTravel 직전에 호출.
    // destination: "town" | "dungeon"
    @PostMapping("/sessions/issue")
    public ResponseEntity<IssueSessionResponse> issueSessionToken(
            @Valid @RequestBody IssueSessionRequest request) {
        return ResponseEntity.ok(matchmakingService.issueSessionToken(request.characterId(), request.destination()));
    }

    @PostMapping("/characters/{characterId}/save")
    public ResponseEntity<Void> saveCharacter(
            @PathVariable Long characterId,
            @Valid @RequestBody SaveCharacterRequest request) {
        gameDataService.saveCharacterData(characterId, request);
        return ResponseEntity.noContent().build();
    }
}
