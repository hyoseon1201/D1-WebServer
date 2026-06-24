package com.d1.server.controller;

import com.d1.server.dto.MatchmakingRequest;
import com.d1.server.dto.MatchmakingResponse;
import com.d1.server.service.MatchmakingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 클라이언트 JWT 인증 필요 (/api/** → JwtInterceptor 적용)
@RestController
@RequestMapping("/api/matchmaking")
@RequiredArgsConstructor
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    @PostMapping("/town")
    public ResponseEntity<MatchmakingResponse> enterTown(
            @RequestAttribute("accountId") Long accountId,
            @Valid @RequestBody MatchmakingRequest request) {
        return ResponseEntity.ok(matchmakingService.enterTown(accountId, request.characterId()));
    }

    // 부하 테스트용 봇 클라이언트가 Town을 거치지 않고 바로 HuntingGround로 접속할 때 사용
    @PostMapping("/huntingground")
    public ResponseEntity<MatchmakingResponse> enterHuntingGround(
            @RequestAttribute("accountId") Long accountId,
            @Valid @RequestBody MatchmakingRequest request) {
        return ResponseEntity.ok(matchmakingService.enterHuntingGround(accountId, request.characterId()));
    }
}
