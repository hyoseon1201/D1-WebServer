package com.d1.server.dto;

public record IssueSessionResponse(
        String sessionToken,
        String serverAddress
) {}
