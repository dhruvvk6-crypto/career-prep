package com.careerprep.entity;

import jakarta.persistence.*;

@Entity
public class InterviewAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "interview_session_id")
    private InterviewSession interviewSession;

    @ManyToOne
    @JoinColumn(name = "interview_question_id")
    private InterviewQuestion interviewQuestion;

    @Column(columnDefinition = "TEXT")
    private String answer;

    public Long getId() {
        return id;
    }

    public InterviewSession getInterviewSession() {
        return interviewSession;
    }

    public void setInterviewSession(InterviewSession interviewSession) {
        this.interviewSession = interviewSession;
    }

    public InterviewQuestion getInterviewQuestion() {
        return interviewQuestion;
    }

    public void setInterviewQuestion(InterviewQuestion interviewQuestion) {
        this.interviewQuestion = interviewQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}