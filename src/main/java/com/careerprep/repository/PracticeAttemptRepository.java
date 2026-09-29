package com.careerprep.repository;

import com.careerprep.entity.PracticeAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PracticeAttemptRepository extends JpaRepository<PracticeAttempt, Long> {
    Optional<PracticeAttempt> findByUserIdAndChallengeId(Long userId, Long challengeId);
    List<PracticeAttempt> findByUserId(Long userId);
}
