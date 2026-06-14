package com.d1.server.repository;

import com.d1.server.entity.CharacterSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterSkillRepository extends JpaRepository<CharacterSkill, Long> {

    List<CharacterSkill> findByCharacterId(Long characterId);

    void deleteByCharacterId(Long characterId);
}
