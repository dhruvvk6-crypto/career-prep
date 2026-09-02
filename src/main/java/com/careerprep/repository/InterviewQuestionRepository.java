package com.careerprep.repository;

import com.careerprep.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewQuestionRepository
        extends JpaRepository<InterviewQuestion, Long> {

    List<InterviewQuestion> findByInterviewSessionIdOrderByQuestionOrder(
            Long sessionId
    );
}