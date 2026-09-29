package com.careerprep.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PracticeAttemptRequest {
    @Size(max = 10000, message = "Practice answers cannot exceed 10000 characters")
    private String answer;

    @NotNull(message = "Completion status is required")
    private Boolean completed;

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
}
