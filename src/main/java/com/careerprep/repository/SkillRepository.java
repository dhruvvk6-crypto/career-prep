package com.careerprep.repository;

import com.careerprep.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByUserId(Long userId);

    boolean existsByUserIdAndName(Long userId, String name);

    Optional<Skill> findByIdAndUserId(Long skillId, Long userId);
}