package com.careerprep.service;

import com.careerprep.dto.*;
import com.careerprep.entity.*;
import com.careerprep.exception.ApiException;
import com.careerprep.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PracticeService {

    private final PracticeChallengeRepository practiceChallengeRepository;
    private final PracticeAttemptRepository practiceAttemptRepository;
    private final LearningPathRepository learningPathRepository;

    public PracticeService(PracticeChallengeRepository practiceChallengeRepository,
                           PracticeAttemptRepository practiceAttemptRepository,
                           LearningPathRepository learningPathRepository) {
        this.practiceChallengeRepository = practiceChallengeRepository;
        this.practiceAttemptRepository = practiceAttemptRepository;
        this.learningPathRepository = learningPathRepository;
    }

    public List<PracticeChallengeResponse> getChallenges(String path) {
        User user = currentUser();
        List<PracticeChallenge> challenges = path == null || path.isBlank()
                ? practiceChallengeRepository.findByActiveTrueOrderByLearningPathNameAscDisplayOrderAsc()
                : practiceChallengeRepository.findByLearningPathTargetRoleAndActiveTrueOrderByDisplayOrder(path);
        Map<Long, PracticeAttempt> attempts = practiceAttemptRepository.findByUserId(user.getId()).stream()
                .collect(Collectors.toMap(attempt -> attempt.getChallenge().getId(), Function.identity()));
        return challenges.stream().map(challenge -> toChallengeResponse(challenge, attempts.get(challenge.getId()))).toList();
    }

    public PracticeAttemptResponse saveAttempt(Long challengeId, PracticeAttemptRequest request) {
        User user = currentUser();
        PracticeChallenge challenge = practiceChallengeRepository.findById(challengeId)
                .filter(PracticeChallenge::isActive)
                .orElseThrow(() -> ApiException.notFound("Practice challenge not found."));
        String answer = request.getAnswer() == null ? "" : request.getAnswer().trim();
        boolean completed = request.getCompleted();
        if (completed && answer.isBlank()) {
            throw ApiException.badRequest("Write an answer before completing a practice challenge.");
        }

        PracticeAttempt attempt = practiceAttemptRepository.findByUserIdAndChallengeId(user.getId(), challengeId)
                .orElseGet(PracticeAttempt::new);
        attempt.setUser(user);
        attempt.setChallenge(challenge);
        attempt.setAnswer(answer);
        attempt.setStatus(completed ? "COMPLETED" : "IN_PROGRESS");
        attempt.setScore(completed ? score(challenge, answer) : null);
        PracticeAttempt saved = practiceAttemptRepository.save(attempt);
        return new PracticeAttemptResponse(challengeId, saved.getStatus(), saved.getScore());
    }

    public PracticeInsightsResponse getInsights() {
        User user = currentUser();
        List<PracticeChallenge> challenges = practiceChallengeRepository.findByActiveTrueOrderByLearningPathNameAscDisplayOrderAsc();
        Map<Long, PracticeAttempt> attempts = practiceAttemptRepository.findByUserId(user.getId()).stream()
                .collect(Collectors.toMap(attempt -> attempt.getChallenge().getId(), Function.identity()));

        List<TopicProgressResponse> topicProgress = challenges.stream()
                .collect(Collectors.groupingBy(challenge -> challenge.getLearningTopic().getId(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(group -> topicProgress(group, attempts))
                .sorted(Comparator.comparing(TopicProgressResponse::getPath).thenComparing(TopicProgressResponse::getTopic))
                .toList();

        List<PathReadinessResponse> readiness = learningPathRepository.findAllByOrderByName().stream()
                .map(path -> pathReadiness(path, challenges, attempts))
                .toList();

        String recommendation = recommendation(topicProgress);
        return new PracticeInsightsResponse(recommendation, readiness, topicProgress);
    }

    private PracticeChallengeResponse toChallengeResponse(PracticeChallenge challenge, PracticeAttempt attempt) {
        return new PracticeChallengeResponse(
                challenge.getId(), challenge.getLearningPath().getTargetRole(), challenge.getLearningTopic().getName(),
                challenge.getTitle(), challenge.getPrompt(), challenge.getLanguage(), challenge.getChallengeType(),
                challenge.getStarterCode(), attempt == null ? "" : attempt.getAnswer(),
                attempt != null && "COMPLETED".equals(attempt.getStatus()), attempt == null ? null : attempt.getScore());
    }

    private TopicProgressResponse topicProgress(List<PracticeChallenge> group, Map<Long, PracticeAttempt> attempts) {
        long completed = group.stream().filter(challenge -> completed(attempts.get(challenge.getId()))).count();
        double average = group.stream()
                .map(challenge -> attempts.get(challenge.getId()))
                .filter(this::completed)
                .map(PracticeAttempt::getScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average().orElse(0);
        PracticeChallenge first = group.getFirst();
        double progress = percentage(completed, group.size());
        return new TopicProgressResponse(first.getLearningPath().getTargetRole(), first.getLearningTopic().getName(),
                completed, group.size(), progress, round(average));
    }

    private PathReadinessResponse pathReadiness(LearningPath path, List<PracticeChallenge> challenges,
                                                Map<Long, PracticeAttempt> attempts) {
        List<PracticeChallenge> group = challenges.stream()
                .filter(challenge -> challenge.getLearningPath().getId().equals(path.getId())).toList();
        long completed = group.stream().filter(challenge -> completed(attempts.get(challenge.getId()))).count();
        double average = group.stream().map(challenge -> attempts.get(challenge.getId()))
                .filter(this::completed).map(PracticeAttempt::getScore).filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue).average().orElse(0);
        double readiness = group.isEmpty() ? 0 : percentage(completed, group.size()) * .55 + average * .45;
        return new PathReadinessResponse(path.getTargetRole(), path.getName(), completed, group.size(), round(readiness));
    }

    private String recommendation(List<TopicProgressResponse> progress) {
        return progress.stream().filter(item -> item.getCompleted() < item.getTotal())
                .min(Comparator.comparingDouble(TopicProgressResponse::getProgress)
                        .thenComparingDouble(TopicProgressResponse::getAverageScore))
                .map(item -> "Practice " + item.getTopic() + " in the " + item.getPath() + " path next.")
                .or(() -> progress.stream().min(Comparator.comparingDouble(TopicProgressResponse::getAverageScore))
                        .map(item -> "Review " + item.getTopic() + " to improve your current score."))
                .orElse("Choose a learning path and complete your first practice challenge.");
    }

    private double score(PracticeChallenge challenge, String answer) {
        String normalized = answer.toLowerCase(Locale.ROOT);
        List<String> keywords = Arrays.stream(challenge.getEvaluationKeywords().split(","))
                .filter(keyword -> !keyword.isBlank()).toList();
        long matches = keywords.stream().filter(normalized::contains).count();
        double concepts = keywords.isEmpty() ? 0 : matches * 75.0 / keywords.size();
        double detail = answer.length() >= 120 ? 25 : answer.length() >= 50 ? 15 : 5;
        return round(Math.min(100, concepts + detail));
    }

    private boolean completed(PracticeAttempt attempt) {
        return attempt != null && "COMPLETED".equals(attempt.getStatus());
    }

    private double percentage(long complete, long total) { return total == 0 ? 0 : complete * 100.0 / total; }
    private double round(double value) { return Math.round(value * 10.0) / 10.0; }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw ApiException.unauthorized("Authentication is required.");
        }
        return user;
    }
}
