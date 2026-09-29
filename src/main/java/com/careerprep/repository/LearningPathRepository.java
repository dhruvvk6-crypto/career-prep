package com.careerprep.repository;

import com.careerprep.entity.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    List<LearningPath> findAllByOrderByName();
    Optional<LearningPath> findByTargetRole(String targetRole);
}
