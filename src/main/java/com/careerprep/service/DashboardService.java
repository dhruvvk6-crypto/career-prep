package com.careerprep.service;

import com.careerprep.dto.DashboardResponse;
import com.careerprep.entity.InterviewResult;
import com.careerprep.entity.InterviewSession;
import com.careerprep.enums.InterviewStatus;
import com.careerprep.repository.SkillRepository;
import com.careerprep.repository.InterviewAnswerRepository;
import com.careerprep.repository.InterviewResultRepository;
import com.careerprep.repository.InterviewSessionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewAnswerRepository interviewAnswerRepository;
    private final InterviewResultRepository interviewResultRepository;
    private final SkillRepository skillRepository;

    public DashboardService(
            InterviewSessionRepository interviewSessionRepository,
            InterviewAnswerRepository interviewAnswerRepository,
            InterviewResultRepository interviewResultRepository,
            SkillRepository skillRepository) {

        this.interviewSessionRepository = interviewSessionRepository;
        this.interviewAnswerRepository = interviewAnswerRepository;
        this.interviewResultRepository = interviewResultRepository;
        this.skillRepository = skillRepository;
    }

    public DashboardResponse getDashboard() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        com.careerprep.entity.User user =
                (com.careerprep.entity.User) authentication.getPrincipal();

        Long userId = user.getId();

        List<com.careerprep.entity.Skill> skills =
                skillRepository.findByUserId(userId);

        List<InterviewSession> interviews =
                interviewSessionRepository.findByUser_Id(userId);


        double javaProgress = 0.0;
        double dsaProgress = 0.0;
        double sqlProgress = 0.0;

        for (com.careerprep.entity.Skill skill : skills) {

            if (skill.getName().equalsIgnoreCase("Java")) {
                javaProgress = skill.getProgress();
            }

            if (skill.getName().equalsIgnoreCase("DSA")) {
                dsaProgress = skill.getProgress();
            }

            if (skill.getName().equalsIgnoreCase("SQL")) {
                sqlProgress = skill.getProgress();
            }
        }


        long interviewsCompleted =
                interviews.stream()
                        .filter(interview ->
                                interview.getStatus() == InterviewStatus.COMPLETED)
                        .count();

        long questionsAttempted = 0;

        double totalScore = 0;
        int scoredInterviews = 0;

        for (InterviewSession interview : interviews) {

            questionsAttempted +=
                    interviewAnswerRepository
                            .findByInterviewSessionId(interview.getId())
                            .size();

            if (interview.getStatus() == InterviewStatus.COMPLETED) {

                InterviewResult result =
                        interviewResultRepository
                                .findByInterviewSessionId(interview.getId())
                                .orElse(null);

                if (result != null && result.getScore() != null) {
                    totalScore += result.getScore();
                    scoredInterviews++;
                }
            }
        }

        double interviewScore =
                scoredInterviews == 0
                        ? 0.0
                        : totalScore / scoredInterviews;

        return new DashboardResponse(
                interviewScore,
                interviewsCompleted,
                (long) questionsAttempted,
                dsaProgress,
                javaProgress,
                sqlProgress
        );
    }
}