package com.careerprep.repository;

import com.careerprep.entity.QuestionBankQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionBankQuestionRepository extends JpaRepository<QuestionBankQuestion, Long> {
    List<QuestionBankQuestion> findTop3ByLearningPathTargetRoleAndDifficultyAndActiveTrueOrderByDisplayOrder(
            String targetRole,
            String difficulty
    );
}
