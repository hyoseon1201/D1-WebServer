package com.d1.server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_LOGIN = "login";
    private static final String TYPE_SESSION = "session";

    private final SecretKey key;
    private final long expirationMs;
    private final long sessionExpirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expirationMs,
                   @Value("${jwt.session-expiration}") long sessionExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.sessionExpirationMs = sessionExpirationMs;
    }

    // ===== 로그인 토큰 (클라이언트, accountId 단위, 장기) =====

    /** accountId를 subject로 담은 로그인 JWT 발급 */
    public String generateToken(Long accountId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(accountId))
                .claim(CLAIM_TYPE, TYPE_LOGIN)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** 서명/만료 검증 후 accountId 반환. 로그인 토큰이 아니면 예외. */
    public Long validateAndGetAccountId(String token) {
        Claims claims = parse(token);
        if (!TYPE_LOGIN.equals(claims.get(CLAIM_TYPE, String.class))) {
            throw new JwtException("로그인 토큰이 아닙니다.");
        }
        return Long.valueOf(claims.getSubject());
    }

    // ===== 세션 토큰 (데디서버 접속용, characterId 단위, 1회용 단기) =====

    /** characterId를 subject로, accountId를 claim으로 담은 세션 토큰 발급 (클라이언트→Town 최초 입장용) */
    public String generateSessionToken(Long accountId, Long characterId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(characterId))
                .claim(CLAIM_TYPE, TYPE_SESSION)
                .claim("accountId", accountId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + sessionExpirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * 서버간 이동용 세션 토큰 발급 (accountId 불필요 — 데디서버가 이미 verify-session으로 신원 확인했음).
     * validateSessionTokenGetCharacterId는 accountId claim을 사용하지 않으므로 호환 가능.
     */
    public String generateSessionTokenForServer(Long characterId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(characterId))
                .claim(CLAIM_TYPE, TYPE_SESSION)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + sessionExpirationMs))
                .signWith(key)
                .compact();
    }

    /** 서명/만료 검증 후 characterId 반환. 세션 토큰이 아니면 예외. */
    public Long validateSessionTokenGetCharacterId(String token) {
        Claims claims = parse(token);
        if (!TYPE_SESSION.equals(claims.get(CLAIM_TYPE, String.class))) {
            throw new JwtException("세션 토큰이 아닙니다.");
        }
        return Long.valueOf(claims.getSubject());
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
