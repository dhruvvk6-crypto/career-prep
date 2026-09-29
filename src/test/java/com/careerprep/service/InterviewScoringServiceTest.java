package com.careerprep.service;

import com.careerprep.dto.InterviewResultResponse;
import com.careerprep.entity.InterviewAnswer;
import com.careerprep.entity.InterviewQuestion;
import com.careerprep.entity.InterviewSession;
import com.careerprep.entity.User;
import com.careerprep.enums.InterviewStatus;
import com.careerprep.repository.InterviewAnswerRepository;
import com.careerprep.repository.InterviewResultRepository;
import com.careerprep.repository.InterviewSessionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewScoringServiceTest {

    @Mock
    private InterviewSessionRepository interviewSessionRepository;
    @Mock
    private InterviewAnswerRepository interviewAnswerRepository;
    @Mock
    private InterviewResultRepository interviewResultRepository;
    @InjectMocks
    private InterviewScoringService scoringService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void resultIncludesConceptSpecificFeedback() {
        User user = new User();
        user.setId(7L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of()));

        InterviewSession session = new InterviewSession();
        session.setId(10L);
        session.setUser(user);
        session.setStatus(InterviewStatus.COMPLETED);
        InterviewQuestion question = new InterviewQuestion();
        question.setId(11L);
        question.setQuestionType("SQL");
        question.setEvaluationKeywords("where,having,group,filter,aggregate");
        InterviewAnswer answer = new InterviewAnswer();
        answer.setInterviewSession(session);
        answer.setInterviewQuestion(question);
        answer.setAnswer("WHERE filters individual rows, while HAVING applies after that stage. "
                + "For example, I use HAVING when I need a second condition.");

        when(interviewSessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(interviewAnswerRepository.findByInterviewSessionId(10L)).thenReturn(List.of(answer));
        when(interviewResultRepository.findByInterviewSessionId(10L)).thenReturn(Optional.empty());
        when(interviewResultRepository.save(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InterviewResultResponse result = scoringService.generateResult(10L);

        assertThat(result.getAnswerFeedback()).hasSize(1);
        assertThat(result.getAnswerFeedback().getFirst().getStrengths())
                .anyMatch(feedback -> feedback.contains("where"));
        assertThat(result.getAnswerFeedback().getFirst().getImprovements())
                .anyMatch(feedback -> feedback.contains("group"));
    }
}
