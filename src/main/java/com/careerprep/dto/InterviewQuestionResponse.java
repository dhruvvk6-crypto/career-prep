package com.careerprep.dto;

public class InterviewQuestionResponse {

    private Long id;
    private String question;
    private String questionType;
    private Integer questionOrder;

    public InterviewQuestionResponse(
            Long id,
            String question,
            String questionType,
            Integer questionOrder) {

        this.id = id;
        this.question = question;
        this.questionType = questionType;
        this.questionOrder = questionOrder;
    }

    public Long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getQuestionType() {
        return questionType;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }
}