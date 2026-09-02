package com.careerprep.dto;

import jakarta.validation.constraints.NotBlank;

public class InterviewRequest {

    @NotBlank(message = "Target role is required")
    private String targetRole;

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }
}