package com.careerprep.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "interview_questions")
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String question;

    @Column(nullable = false, length = 50)
    private String questionType;

    @Column(nullable = false)
    private Integer questionOrder;

    @Column(nullable = false, length = 1000)
    private String evaluationKeywords;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession interviewSession;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getEvaluationKeywords() {
        return evaluationKeywords;
    }

    public void setEvaluationKeywords(String evaluationKeywords) {
        this.evaluationKeywords = evaluationKeywords;
    }

    public InterviewSession getInterviewSession() {
        return interviewSession;
    }

    public void setInterviewSession(InterviewSession interviewSession) {
        this.interviewSession = interviewSession;
    }
}
