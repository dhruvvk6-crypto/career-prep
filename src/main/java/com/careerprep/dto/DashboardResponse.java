package com.careerprep.dto;

public class DashboardResponse {

    private Double interviewScore;
    private Long interviewsCompleted;
    private Long questionsAttempted;

    private Double dsaProgress;
    private Double javaProgress;
    private Double sqlProgress;

    public DashboardResponse(
            Double interviewScore,
            Long interviewsCompleted,
            Long questionsAttempted,
            Double dsaProgress,
            Double javaProgress,
            Double sqlProgress) {

        this.interviewScore = interviewScore;
        this.interviewsCompleted = interviewsCompleted;
        this.questionsAttempted = questionsAttempted;

        this.dsaProgress = dsaProgress;
        this.javaProgress = javaProgress;
        this.sqlProgress = sqlProgress;
    }

    public Double getInterviewScore() {
        return interviewScore;
    }

    public Long getInterviewsCompleted() {
        return interviewsCompleted;
    }

    public Long getQuestionsAttempted() {
        return questionsAttempted;
    }

    public Double getDsaProgress() {
        return dsaProgress;
    }

    public Double getJavaProgress() {
        return javaProgress;
    }

    public Double getSqlProgress() {
        return sqlProgress;
    }
}