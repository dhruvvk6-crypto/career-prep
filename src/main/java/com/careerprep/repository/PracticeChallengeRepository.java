package com.careerprep.repository;

import com.careerprep.entity.PracticeChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PracticeChallengeRepository extends JpaRepository<PracticeChallenge, Long> {
    List<PracticeChallenge> findByActiveTrueOrderByLearningPathNameAscDisplayOrderAsc();
    List<PracticeChallenge> findByLearningPathTargetRoleAndActiveTrueOrderByDisplayOrder(String targetRole);
}
