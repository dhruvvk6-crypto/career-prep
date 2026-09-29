package com.careerprep.repository;

import com.careerprep.entity.LearningTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningTopicRepository extends JpaRepository<LearningTopic, Long> {
    List<LearningTopic> findByLearningPathIdOrderByDisplayOrder(Long learningPathId);
    Optional<LearningTopic> findByLearningPathTargetRoleAndName(String targetRole, String name);
}
