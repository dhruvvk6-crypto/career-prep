package com.careerprep.dto;

import java.util.List;

public class InterviewResponse {

    private Long id;
    private String targetRole;
    private String difficulty;
    private String status;
    private List<InterviewQuestionResponse> questions;

    public InterviewResponse(
            Long id,
            String targetRole,
            String difficulty,
            String status,
            List<InterviewQuestionResponse> questions) {

        this.id = id;
        this.targetRole = targetRole;
        this.difficulty = difficulty;
        this.status = status;
        this.questions = questions;
    }

    public Long getId() {
        return id;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getStatus() {
        return status;
    }

    public List<InterviewQuestionResponse> getQuestions() {
        return questions;
    }
}
