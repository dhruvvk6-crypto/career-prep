package com.careerprep.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class SkillProgressUpdateRequest {

    @NotNull(message = "Progress is required")
    @DecimalMin(value = "0.0", message = "Progress must be at least 0")
    @DecimalMax(value = "100.0", message = "Progress cannot exceed 100")
    private Double progress;

    public SkillProgressUpdateRequest() {
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }
}
