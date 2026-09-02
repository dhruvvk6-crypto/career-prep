package com.careerprep.service;

import com.careerprep.dto.InterviewResultResponse;
import com.careerprep.entity.InterviewAnswer;
import com.careerprep.entity.InterviewResult;
import com.careerprep.entity.InterviewSession;
import com.careerprep.entity.User;
import com.careerprep.enums.InterviewStatus;
import com.careerprep.repository.InterviewAnswerRepository;
import com.careerprep.repository.InterviewResultRepository;
import com.careerprep.repository.InterviewSessionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        InterviewSession interview =
                interviewSessionRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new RuntimeException("Interview not found"));

        if (!interview.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this interview");
        }

        if (interview.getStatus() != InterviewStatus.COMPLETED) {
            throw new RuntimeException(
                    "Interview is not completed yet");
        }

        List<InterviewAnswer> answers =
                interviewAnswerRepository.findByInterviewSessionId(interviewId);

        if (answers.isEmpty()) {
            throw new RuntimeException(
                    "No answers found for this interview");
        }

        double score = calculateScore(answers);

        String strongAreas = findStrongAreas(answers);
        String improvementAreas = findImprovementAreas(answers);

        InterviewResult result =
                interviewResultRepository
                        .findByInterviewSessionId(interviewId)
                        .orElseGet(InterviewResult::new);

        result.setInterviewSession(interview);
        result.setScore(score);
        result.setStrongAreas(strongAreas);
        result.setImprovementAreas(improvementAreas);

        InterviewResult savedResult =
                interviewResultRepository.save(result);

        return new InterviewResultResponse(
                savedResult.getId(),
                interviewId,
                savedResult.getScore(),
                savedResult.getStrongAreas(),
                savedResult.getImprovementAreas()
        );
    }

    private double calculateScore(List<InterviewAnswer> answers) {

        double totalScore = 0;

        for (InterviewAnswer answer : answers) {

            String text = answer.getAnswer();

            if (text == null || text.trim().isEmpty()) {
                continue;
            }

            int length = text.trim().length();

            if (length < 30) {
                totalScore += 40;
            } else if (length < 60) {
                totalScore += 60;
            } else if (length < 100) {
                totalScore += 80;
            } else {
                totalScore += 100;
            }
        }

        return totalScore / answers.size();
    }

    private String findStrongAreas(List<InterviewAnswer> answers) {

        int strongAnswers = 0;

        for (InterviewAnswer answer : answers) {

            if (answer.getAnswer() != null &&
                    answer.getAnswer().trim().length() >= 60) {

                strongAnswers++;
            }
        }

        if (strongAnswers == answers.size()) {
            return "Technical concepts and detailed explanations";
        }

        if (strongAnswers > 0) {
            return "Technical concepts";
        }

        return "No strong area identified yet";
    }



    private String findImprovementAreas(List<InterviewAnswer> answers) {

        int weakAnswers = 0;

        for (InterviewAnswer answer : answers) {

            if (answer.getAnswer() == null ||
                    answer.getAnswer().trim().length() < 30) {

                weakAnswers++;
            }
        }

        if (weakAnswers == 0) {
            return "No major improvement area identified";
        }

        return "Answer depth and explanation";
    }
}