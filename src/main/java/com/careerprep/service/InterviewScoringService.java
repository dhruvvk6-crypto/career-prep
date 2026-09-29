package com.careerprep.service;

import com.careerprep.dto.AnswerFeedbackResponse;
import com.careerprep.dto.InterviewResultResponse;
import com.careerprep.entity.InterviewAnswer;
import com.careerprep.entity.InterviewResult;
import com.careerprep.entity.InterviewSession;
import com.careerprep.entity.User;
import com.careerprep.enums.InterviewStatus;
import com.careerprep.exception.ApiException;
import com.careerprep.repository.InterviewAnswerRepository;
import com.careerprep.repository.InterviewResultRepository;
import com.careerprep.repository.InterviewSessionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class InterviewScoringService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewAnswerRepository interviewAnswerRepository;
    private final InterviewResultRepository interviewResultRepository;

    public InterviewScoringService(
            InterviewSessionRepository interviewSessionRepository,
            InterviewAnswerRepository interviewAnswerRepository,
            InterviewResultRepository interviewResultRepository) {
        this.interviewSessionRepository = interviewSessionRepository;
        this.interviewAnswerRepository = interviewAnswerRepository;
        this.interviewResultRepository = interviewResultRepository;
    }

    public InterviewResultResponse generateResult(Long interviewId) {
        InterviewSession interview = findOwnedInterview(interviewId);
        if (interview.getStatus() != InterviewStatus.COMPLETED) {
            throw ApiException.conflict("Complete the interview before requesting its result.");
        }

        List<InterviewAnswer> answers = interviewAnswerRepository.findByInterviewSessionId(interviewId);
        if (answers.isEmpty()) {
            throw ApiException.badRequest("No answers found for this interview.");
        }

        List<ScoredAnswer> scoredAnswers = answers.stream()
                .map(answer -> new ScoredAnswer(answer, evaluateAnswer(answer)))
                .toList();
        double score = scoredAnswers.stream()
                .mapToDouble(answer -> answer.evaluation().score())
                .average()
                .orElse(0);

        InterviewResult result = interviewResultRepository.findByInterviewSessionId(interviewId)
                .orElseGet(InterviewResult::new);
        result.setInterviewSession(interview);
        result.setScore(score);
        result.setStrongAreas(findStrongAreas(scoredAnswers));
        result.setImprovementAreas(findImprovementAreas(scoredAnswers));

        InterviewResult savedResult = interviewResultRepository.save(result);
        return new InterviewResultResponse(
                savedResult.getId(), interviewId, savedResult.getScore(),
                savedResult.getStrongAreas(), savedResult.getImprovementAreas(),
                scoredAnswers.stream().map(this::toFeedback).toList());
    }

    private AnswerEvaluation evaluateAnswer(InterviewAnswer answer) {
        String text = answer.getAnswer().trim().toLowerCase(Locale.ROOT);
        int wordCount = text.split("\\s+").length;

        double depthScore = wordCount < 15 ? 10 : wordCount < 35 ? 25 : wordCount < 70 ? 35 : 45;
        List<String> expectedKeywords = java.util.Arrays.stream(
                        answer.getInterviewQuestion().getEvaluationKeywords().split(","))
                .filter(keyword -> !keyword.isBlank())
                .toList();
        List<String> matchedKeywords = expectedKeywords.stream().filter(text::contains).toList();
        List<String> missingKeywords = expectedKeywords.stream().filter(keyword -> !text.contains(keyword)).toList();
        double conceptScore = expectedKeywords.isEmpty() ? 0 : (45.0 * matchedKeywords.size() / expectedKeywords.size());
        boolean includesExplanation = text.contains("because")
                || text.contains("for example")
                || text.contains("for instance");
        double explanationScore = includesExplanation ? 10 : 0;

        return new AnswerEvaluation(
                Math.round(Math.min(100, depthScore + conceptScore + explanationScore) * 10.0) / 10.0,
                wordCount,
                matchedKeywords,
                missingKeywords,
                includesExplanation
        );
    }

    private String findStrongAreas(List<ScoredAnswer> answers) {
        List<String> strongTypes = answers.stream()
                .filter(answer -> answer.evaluation().score() >= 75)
                .map(answer -> answer.answer().getInterviewQuestion().getQuestionType())
                .distinct()
                .toList();
        return strongTypes.isEmpty()
                ? "No strong area identified yet"
                : "Strong response areas: " + String.join(", ", strongTypes);
    }

    private String findImprovementAreas(List<ScoredAnswer> answers) {
        return answers.stream()
                .min(Comparator.comparingDouble(answer -> answer.evaluation().score()))
                .filter(answer -> answer.evaluation().score() < 75)
                .map(answer -> "Improve answer depth and role-specific detail for "
                        + answer.answer().getInterviewQuestion().getQuestionType())
                .orElse("No major improvement area identified");
    }

    private InterviewSession findOwnedInterview(Long interviewId) {
        InterviewSession interview = interviewSessionRepository.findById(interviewId)
                .orElseThrow(() -> ApiException.notFound("Interview not found."));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw ApiException.unauthorized("Authentication is required.");
        }
        if (!interview.getUser().getId().equals(user.getId())) {
            throw ApiException.forbidden("You are not allowed to access this interview.");
        }
        return interview;
    }

    private AnswerFeedbackResponse toFeedback(ScoredAnswer scoredAnswer) {
        AnswerEvaluation evaluation = scoredAnswer.evaluation();
        List<String> strengths = new java.util.ArrayList<>();
        List<String> improvements = new java.util.ArrayList<>();

        if (evaluation.wordCount() >= 35) {
            strengths.add("Provides enough detail to assess the answer.");
        } else {
            improvements.add("Add a clearer explanation with concrete details.");
        }
        if (!evaluation.matchedKeywords().isEmpty()) {
            strengths.add("Covers relevant concepts: " + String.join(", ", evaluation.matchedKeywords()) + ".");
        }
        if (!evaluation.missingKeywords().isEmpty()) {
            improvements.add("Address these concepts: " + String.join(", ", evaluation.missingKeywords()) + ".");
        }
        if (evaluation.includesExplanation()) {
            strengths.add("Uses an example or rationale to connect the ideas.");
        } else {
            improvements.add("Explain why your approach works or include a short example.");
        }
        if (strengths.isEmpty()) {
            strengths.add("You submitted an answer; build on it with role-specific concepts.");
        }

        return new AnswerFeedbackResponse(
                scoredAnswer.answer().getInterviewQuestion().getId(),
                scoredAnswer.answer().getInterviewQuestion().getQuestionType(),
                evaluation.score(),
                strengths,
                improvements
        );
    }

    private record ScoredAnswer(InterviewAnswer answer, AnswerEvaluation evaluation) {
    }

    private record AnswerEvaluation(
            double score,
            int wordCount,
            List<String> matchedKeywords,
            List<String> missingKeywords,
            boolean includesExplanation) {
    }
}
