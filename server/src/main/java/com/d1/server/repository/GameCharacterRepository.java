package com.d1.server.repository;

import com.d1.server.entity.GameCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameCharacterRepository extends JpaRepository<GameCharacter, Long> {

    List<GameCharacter> findByAccountId(Long accountId);

    boolean existsByName(String name);
}
