package com.careerprep.dto;

public class PracticeAttemptResponse {
    private final Long challengeId;
    private final String status;
    private final Double score;

    public PracticeAttemptResponse(Long challengeId, String status, Double score) {
        this.challengeId = challengeId;
        this.status = status;
        this.score = score;
    }

    public Long getChallengeId() { return challengeId; }
    public String getStatus() { return status; }
    public Double getScore() { return score; }
}
