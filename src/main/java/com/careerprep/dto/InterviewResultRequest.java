package com.careerprep.dto;

public class InterviewResultRequest {

    private Long interviewId;
    private Double score;
    private String strongAreas;
    private String improvementAreas;

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getStrongAreas() {
        return strongAreas;
    }

    public void setStrongAreas(String strongAreas) {
        this.strongAreas = strongAreas;
    }

    public String getImprovementAreas() {
        return improvementAreas;
    }

    public void setImprovementAreas(String improvementAreas) {
        this.improvementAreas = improvementAreas;
    }
}