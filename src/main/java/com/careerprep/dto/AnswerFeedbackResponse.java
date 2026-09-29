package com.careerprep.dto;

import java.util.List;

public class AnswerFeedbackResponse {

    private final Long questionId;
    private final String category;
    private final double score;
    private final List<String> strengths;
    private final List<String> improvements;

    public AnswerFeedbackResponse(
            Long questionId,
            String category,
            double score,
            List<String> strengths,
            List<String> improvements) {
        this.questionId = questionId;
        this.category = category;
        this.score = score;
        this.strengths = strengths;
        this.improvements = improvements;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getCategory() {
        return category;
    }

    public double getScore() {
        return score;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public List<String> getImprovements() {
        return improvements;
    }
}
