package com.careerprep.dto;

public class InterviewResultResponse {

    private Long id;
    private Long interviewId;
    private Double score;
    private String strongAreas;
    private String improvementAreas;

    public InterviewResultResponse(
            Long id,
            Long interviewId,
            Double score,
            String strongAreas,
            String improvementAreas) {

        this.id = id;
        this.interviewId = interviewId;
        this.score = score;
        this.strongAreas = strongAreas;
        this.improvementAreas = improvementAreas;
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
}