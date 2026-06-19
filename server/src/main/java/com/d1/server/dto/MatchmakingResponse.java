package com.d1.server.dto;

// 클라이언트가 이 주소로 ClientTravel하면서 sessionToken을 URL 옵션으로 붙인다.
public record MatchmakingResponse(String serverAddress, String sessionToken) {}
