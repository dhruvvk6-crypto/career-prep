package com.careerprep.service;

import com.careerprep.dto.InterviewRequest;
import com.careerprep.dto.InterviewResponse;
import com.careerprep.entity.InterviewAnswer;
import com.careerprep.entity.InterviewQuestion;
import com.careerprep.entity.InterviewSession;
import com.careerprep.entity.QuestionBankQuestion;
import com.careerprep.entity.User;
import com.careerprep.enums.InterviewStatus;
import com.careerprep.exception.ApiException;
import com.careerprep.repository.InterviewAnswerRepository;
import com.careerprep.repository.InterviewQuestionRepository;
import com.careerprep.repository.InterviewSessionRepository;
import com.careerprep.repository.LearningPathRepository;
import com.careerprep.repository.QuestionBankQuestionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewSessionRepository interviewSessionRepository;
    @Mock
    private InterviewQuestionRepository interviewQuestionRepository;
    @Mock
    private InterviewAnswerRepository interviewAnswerRepository;
    @Mock
    private LearningPathRepository learningPathRepository;
    @Mock
    private QuestionBankQuestionRepository questionBankQuestionRepository;
    @InjectMocks
    private InterviewService interviewService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createInterviewGeneratesQuestionsForRequestedRoleAndDifficulty() {
        authenticate(user(7L));
        InterviewRequest request = new InterviewRequest();
        request.setTargetRole("Java Developer");
        request.setDifficulty("intermediate");
        when(interviewSessionRepository.save(any(InterviewSession.class))).thenAnswer(invocation -> {
            InterviewSession session = invocation.getArgument(0);
            session.setId(10L);
            return session;
        });
        when(interviewQuestionRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<InterviewQuestion> questions = invocation.getArgument(0);
            for (int index = 0; index < questions.size(); index++) {
                questions.get(index).setId((long) index + 1);
            }
            return questions;
        });
        when(questionBankQuestionRepository
                .findTop3ByLearningPathTargetRoleAndDifficultyAndActiveTrueOrderByDisplayOrder(
                        "Java Developer", "INTERMEDIATE"))
                .thenReturn(List.of(
                        bankQuestion("How do HashMap collisions work?", "JAVA", 1),
                        bankQuestion("Explain transactions.", "SPRING", 2),
                        bankQuestion("Design secure APIs.", "SECURITY", 3)
                ));

        InterviewResponse response = interviewService.createInterview(request);

        ArgumentCaptor<InterviewSession> savedSession = ArgumentCaptor.forClass(InterviewSession.class);
        verify(interviewSessionRepository).save(savedSession.capture());
        assertThat(savedSession.getValue().getDifficulty()).isEqualTo("INTERMEDIATE");
        assertThat(response.getQuestions()).hasSize(3);
        assertThat(response.getQuestions().getFirst().getQuestion()).contains("HashMap collisions");
    }

    @Test
    void completeInterviewRejectsMissingAnswers() {
        User user = user(7L);
        authenticate(user);
        InterviewSession session = session(10L, user);
        InterviewQuestion firstQuestion = question(1L, session);
        InterviewQuestion secondQuestion = question(2L, session);
        InterviewAnswer firstAnswer = new InterviewAnswer();
        firstAnswer.setInterviewQuestion(firstQuestion);
        firstAnswer.setInterviewSession(session);
        firstAnswer.setAnswer("A valid answer with enough detail.");

        when(interviewSessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(interviewQuestionRepository.findByInterviewSessionIdOrderByQuestionOrder(10L))
                .thenReturn(List.of(firstQuestion, secondQuestion));
        when(interviewAnswerRepository.findByInterviewSessionId(10L)).thenReturn(List.of(firstAnswer));

        assertThatThrownBy(() -> interviewService.completeInterview(10L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Submit one answer for every interview question before completing it.");
    }

    @Test
    void createInterviewUsesTheDedicatedSqlQuestionBank() {
        authenticate(user(7L));
        InterviewRequest request = new InterviewRequest();
        request.setTargetRole("SQL");
        request.setDifficulty("BEGINNER");
        when(interviewSessionRepository.save(any(InterviewSession.class))).thenAnswer(invocation -> {
            InterviewSession session = invocation.getArgument(0);
            session.setId(10L);
            return session;
        });
        when(interviewQuestionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(questionBankQuestionRepository
                .findTop3ByLearningPathTargetRoleAndDifficultyAndActiveTrueOrderByDisplayOrder("SQL", "BEGINNER"))
                .thenReturn(List.of(
                        bankQuestion("What is the difference between WHERE and HAVING?", "SQL", 1),
                        bankQuestion("Explain an INNER JOIN.", "SQL", 2),
                        bankQuestion("What is normalization?", "DATA_MODELING", 3)
                ));

        InterviewResponse response = interviewService.createInterview(request);

        assertThat(response.getQuestions().getFirst().getQuestion()).contains("WHERE and HAVING");
        assertThat(response.getQuestions().get(1).getQuestionType()).isEqualTo("SQL");
    }

    private void authenticate(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of())
        );
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private InterviewSession session(Long id, User user) {
        InterviewSession session = new InterviewSession();
        session.setId(id);
        session.setUser(user);
        session.setStatus(InterviewStatus.IN_PROGRESS);
        session.setTargetRole("Java Developer");
        session.setDifficulty("INTERMEDIATE");
        return session;
    }

    private InterviewQuestion question(Long id, InterviewSession session) {
        InterviewQuestion question = new InterviewQuestion();
        question.setId(id);
        question.setInterviewSession(session);
        return question;
    }

    private QuestionBankQuestion bankQuestion(String text, String category, int order) {
        QuestionBankQuestion question = new QuestionBankQuestion();
        question.setQuestion(text);
        question.setCategory(category);
        question.setEvaluationKeywords("concept,example,detail");
        question.setDisplayOrder(order);
        return question;
    }
}
