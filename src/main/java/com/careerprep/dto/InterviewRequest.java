package com.careerprep.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class InterviewRequest {

    @NotBlank(message = "Target role is required")
    @Size(max = 100, message = "Target role cannot exceed 100 characters")
    private String targetRole;

    @NotBlank(message = "Difficulty is required")
    @Pattern(
            regexp = "(?i)BEGINNER|INTERMEDIATE|ADVANCED",
            message = "Difficulty must be BEGINNER, INTERMEDIATE, or ADVANCED"
    )
    private String difficulty;

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
