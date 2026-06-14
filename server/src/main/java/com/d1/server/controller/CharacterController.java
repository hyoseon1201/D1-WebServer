package com.d1.server.controller;

import com.d1.server.dto.CharacterResponse;
import com.d1.server.dto.CreateCharacterRequest;
import com.d1.server.service.CharacterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    @GetMapping
    public ResponseEntity<List<CharacterResponse>> getCharacters(
            @RequestAttribute("accountId") Long accountId) {
        return ResponseEntity.ok(characterService.getCharacters(accountId));
    }

    @PostMapping
    public ResponseEntity<CharacterResponse> createCharacter(
            @RequestAttribute("accountId") Long accountId,
            @Valid @RequestBody CreateCharacterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(characterService.createCharacter(accountId, request));
    }
}
