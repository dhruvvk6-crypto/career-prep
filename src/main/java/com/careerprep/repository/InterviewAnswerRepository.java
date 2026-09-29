package com.careerprep.repository;

import com.careerprep.entity.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, Long> {

    List<InterviewAnswer> findByInterviewSessionId(Long interviewId);

    Optional<InterviewAnswer> findByInterviewSessionIdAndInterviewQuestionId(
            Long interviewId,
            Long questionId
    );
}
