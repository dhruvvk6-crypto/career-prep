package com.careerprep.dto;

import java.util.List;

public class InterviewResultResponse {

    private Long id;
    private Long interviewId;
    private Double score;
    private String strongAreas;
    private String improvementAreas;
    private List<AnswerFeedbackResponse> answerFeedback;

    public InterviewResultResponse(
            Long id,
            Long interviewId,
            Double score,
            String strongAreas,
            String improvementAreas,
            List<AnswerFeedbackResponse> answerFeedback) {

        this.id = id;
        this.interviewId = interviewId;
        this.score = score;
        this.strongAreas = strongAreas;
        this.improvementAreas = improvementAreas;
        this.answerFeedback = answerFeedback;
    }

    public Long getId() {
        return id;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public Double getScore() {
        return score;
    }

    public String getStrongAreas() {
        return strongAreas;
    }

    public String getImprovementAreas() {
        return improvementAreas;
    }

    public List<AnswerFeedbackResponse> getAnswerFeedback() {
        return answerFeedback;
    }
}
