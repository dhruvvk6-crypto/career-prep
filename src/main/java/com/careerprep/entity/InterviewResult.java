package com.careerprep.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "interview_results")
public class InterviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "interview_id", nullable = false, unique = true)
    private InterviewSession interviewSession;

    private Double score;

    @Column(columnDefinition = "TEXT")
    private String strongAreas;

    @Column(columnDefinition = "TEXT")
    private String improvementAreas;

    public InterviewResult() {
    }

    public Long getId() {
        return id;
    }

    public InterviewSession getInterviewSession() {
        return interviewSession;
    }

    public void setInterviewSession(InterviewSession interviewSession) {
        this.interviewSession = interviewSession;
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