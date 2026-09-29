package com.careerprep.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InterviewAnswerUpdateRequest {

    @NotBlank(message = "Answer is required")
    @Size(max = 5000, message = "Answer cannot exceed 5000 characters")
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
