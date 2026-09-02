package com.careerprep.dto;

public class InterviewAnswerResponse {

    private Long id;
    private Long questionId;
    private String answer;

    public InterviewAnswerResponse(Long id, Long questionId, String answer) {
        this.id = id;
        this.questionId = questionId;
        this.answer = answer;
    }

    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getAnswer() {
        return answer;
    }
}