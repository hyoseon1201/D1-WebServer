package com.d1.server.service;

import com.d1.server.dto.IssueSessionResponse;
import com.d1.server.dto.MatchmakingResponse;
import com.d1.server.entity.GameCharacter;
import com.d1.server.exception.ApiException;
import com.d1.server.repository.GameCharacterRepository;
import com.d1.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchmakingService {

    private final GameCharacterRepository characterRepository;
    private final JwtUtil jwtUtil;

    @Value("${game-server.town-address}")
    private String townAddress;

    @Value("${game-server.dungeon-address}")
    private String dungeonAddress;

    @Value("${game-server.hunting-ground-address}")
    private String huntingGroundAddress;

    /**
     * Town 입장 요청. 캐릭터 소유권을 검증한 뒤 1회용 세션 토큰을 발급하고
     * 접속할 데디서버 주소와 함께 반환한다.
     */
    @Transactional(readOnly = true)
    public MatchmakingResponse enterTown(Long accountId, Long characterId) {
        return issueMatchmakingToken(accountId, characterId, townAddress);
    }

    /**
     * HuntingGround 입장 요청. 부하 테스트용 봇 클라이언트가 Town을 거치지 않고
     * 바로 HuntingGround 데디서버로 접속할 때 사용. 소유권 검증은 enterTown과 동일.
     */
    @Transactional(readOnly = true)
    public MatchmakingResponse enterHuntingGround(Long accountId, Long characterId) {
        return issueMatchmakingToken(accountId, characterId, huntingGroundAddress);
    }

    private MatchmakingResponse issueMatchmakingToken(Long accountId, Long characterId, String address) {
        GameCharacter character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "캐릭터를 찾을 수 없습니다."));

        // 소유권 검증 — 본인 계정의 캐릭터만 입장 가능 (characterId 사칭 차단)
        if (!character.getAccountId().equals(accountId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "본인 캐릭터가 아닙니다.");
        }

        String sessionToken = jwtUtil.generateSessionToken(accountId, characterId);
        return new MatchmakingResponse(address, sessionToken);
    }

    /**
     * 서버간 이동용 세션 토큰 발급 (데디서버 전용).
     * 클라이언트 소유권 검증 없음 — 데디서버가 이미 verify-session으로 신원 확인했으므로 신뢰.
     * destination: "town" → townAddress, "dungeon" → dungeonAddress, "huntingground" → huntingGroundAddress.
     */
    public IssueSessionResponse issueSessionToken(Long characterId, String destination) {
        String token = jwtUtil.generateSessionTokenForServer(characterId);
        String address;
        if ("dungeon".equalsIgnoreCase(destination)) {
            address = dungeonAddress;
        } else if ("huntingground".equalsIgnoreCase(destination)) {
            address = huntingGroundAddress;
        } else {
            address = townAddress;
        }
        return new IssueSessionResponse(token, address);
    }
}
