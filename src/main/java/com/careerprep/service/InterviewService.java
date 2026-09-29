package com.careerprep.service;

import com.careerprep.dto.InterviewAnswerRequest;
import com.careerprep.dto.InterviewAnswerResponse;
import com.careerprep.dto.InterviewAnswerUpdateRequest;
import com.careerprep.dto.InterviewConfigurationResponse;
import com.careerprep.dto.InterviewQuestionResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class InterviewService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final InterviewAnswerRepository interviewAnswerRepository;
    private final LearningPathRepository learningPathRepository;
    private final QuestionBankQuestionRepository questionBankQuestionRepository;

    public InterviewService(
            InterviewSessionRepository interviewSessionRepository,
            InterviewQuestionRepository interviewQuestionRepository,
            InterviewAnswerRepository interviewAnswerRepository,
            LearningPathRepository learningPathRepository,
            QuestionBankQuestionRepository questionBankQuestionRepository) {
        this.interviewSessionRepository = interviewSessionRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.interviewAnswerRepository = interviewAnswerRepository;
        this.learningPathRepository = learningPathRepository;
        this.questionBankQuestionRepository = questionBankQuestionRepository;
    }

    public InterviewResponse createInterview(InterviewRequest request) {
        User user = currentUser();
        InterviewSession session = new InterviewSession();
        session.setTargetRole(request.getTargetRole().trim());
        session.setDifficulty(request.getDifficulty().trim().toUpperCase(Locale.ROOT));
        session.setStatus(InterviewStatus.IN_PROGRESS);
        session.setUser(user);

        InterviewSession savedSession = interviewSessionRepository.save(session);
        List<InterviewQuestion> savedQuestions = interviewQuestionRepository.saveAll(
                generateQuestions(savedSession)
        );
        return buildInterviewResponse(savedSession, savedQuestions);
    }

    public InterviewConfigurationResponse getInterviewConfiguration() {
        return new InterviewConfigurationResponse(
                learningPathRepository.findAllByOrderByName().stream()
                        .map(path -> path.getTargetRole())
                        .toList(),
                List.of("BEGINNER", "INTERMEDIATE", "ADVANCED")
        );
    }

    public InterviewResponse getInterview(Long id) {
        return convertToResponse(findOwnedInterview(id));
    }

    public InterviewAnswerResponse submitAnswer(Long interviewId, InterviewAnswerRequest request) {
        InterviewSession interview = findOwnedInterview(interviewId);
        requireInProgress(interview);

        InterviewQuestion question = interviewQuestionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> ApiException.notFound("Question not found."));
        if (!question.getInterviewSession().getId().equals(interviewId)) {
            throw ApiException.badRequest("Question does not belong to this interview.");
        }
        if (interviewAnswerRepository
                .findByInterviewSessionIdAndInterviewQuestionId(interviewId, request.getQuestionId())
                .isPresent()) {
            throw ApiException.conflict("An answer has already been submitted for this question.");
        }

        InterviewAnswer answer = new InterviewAnswer();
        answer.setInterviewSession(interview);
        answer.setInterviewQuestion(question);
        answer.setAnswer(request.getAnswer().trim());
        return toAnswerResponse(interviewAnswerRepository.save(answer));
    }

    public List<InterviewAnswerResponse> getAnswers(Long interviewId) {
        findOwnedInterview(interviewId);
        return interviewAnswerRepository.findByInterviewSessionId(interviewId).stream()
                .map(this::toAnswerResponse)
                .toList();
    }

    public InterviewAnswerResponse updateAnswer(
            Long interviewId,
            Long answerId,
            InterviewAnswerUpdateRequest request) {
        InterviewSession interview = findOwnedInterview(interviewId);
        requireInProgress(interview);

        InterviewAnswer answer = interviewAnswerRepository.findById(answerId)
                .orElseThrow(() -> ApiException.notFound("Answer not found."));
        if (!answer.getInterviewSession().getId().equals(interviewId)) {
            throw ApiException.badRequest("Answer does not belong to this interview.");
        }

        answer.setAnswer(request.getAnswer().trim());
        return toAnswerResponse(interviewAnswerRepository.save(answer));
    }

    public InterviewResponse completeInterview(Long id) {
        InterviewSession interview = findOwnedInterview(id);
        requireInProgress(interview);

        List<InterviewQuestion> questions = interviewQuestionRepository
                .findByInterviewSessionIdOrderByQuestionOrder(id);
        List<InterviewAnswer> answers = interviewAnswerRepository.findByInterviewSessionId(id);
        Set<Long> answeredQuestionIds = answers.stream()
                .map(answer -> answer.getInterviewQuestion().getId())
                .collect(java.util.stream.Collectors.toSet());

        if (questions.isEmpty() || answeredQuestionIds.size() != questions.size()) {
            throw ApiException.badRequest("Submit one answer for every interview question before completing it.");
        }

        interview.setStatus(InterviewStatus.COMPLETED);
        return convertToResponse(interviewSessionRepository.save(interview));
    }

    private List<InterviewQuestion> generateQuestions(InterviewSession session) {
        List<QuestionBankQuestion> bankQuestions = questionBankQuestionRepository
                .findTop3ByLearningPathTargetRoleAndDifficultyAndActiveTrueOrderByDisplayOrder(
                        session.getTargetRole(), session.getDifficulty());
        if (bankQuestions.size() < 3) {
            throw ApiException.badRequest("No complete question set is available for this learning path and difficulty.");
        }

        return bankQuestions.stream()
                .map(bankQuestion -> toQuestion(session, bankQuestion))
                .toList();
    }

    private InterviewQuestion toQuestion(InterviewSession session, QuestionBankQuestion bankQuestion) {
        InterviewQuestion question = new InterviewQuestion();
        question.setQuestion(bankQuestion.getQuestion());
        question.setQuestionType(bankQuestion.getCategory());
        question.setEvaluationKeywords(bankQuestion.getEvaluationKeywords());
        question.setQuestionOrder(bankQuestion.getDisplayOrder());
        question.setInterviewSession(session);
        return question;
    }

    private InterviewSession findOwnedInterview(Long id) {
        InterviewSession interview = interviewSessionRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Interview not found."));
        if (!interview.getUser().getId().equals(currentUser().getId())) {
            throw ApiException.forbidden("You are not allowed to access this interview.");
        }
        return interview;
    }

    private void requireInProgress(InterviewSession interview) {
        if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw ApiException.conflict("This interview is already completed.");
        }
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw ApiException.unauthorized("Authentication is required.");
        }
        return user;
    }

    private InterviewResponse convertToResponse(InterviewSession session) {
        return buildInterviewResponse(session, interviewQuestionRepository
                .findByInterviewSessionIdOrderByQuestionOrder(session.getId()));
    }

    private InterviewResponse buildInterviewResponse(InterviewSession session, List<InterviewQuestion> questions) {
        List<InterviewQuestionResponse> questionResponses = questions.stream()
                .map(question -> new InterviewQuestionResponse(
                        question.getId(), question.getQuestion(), question.getQuestionType(), question.getQuestionOrder()))
                .toList();
        return new InterviewResponse(
                session.getId(), session.getTargetRole(), session.getDifficulty(),
                session.getStatus().name(), questionResponses);
    }

    private InterviewAnswerResponse toAnswerResponse(InterviewAnswer answer) {
        return new InterviewAnswerResponse(
                answer.getId(), answer.getInterviewQuestion().getId(), answer.getAnswer());
    }

}
