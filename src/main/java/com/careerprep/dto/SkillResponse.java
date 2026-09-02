package com.careerprep.dto;

public class SkillResponse {

    private Long id;
    private String name;
    private Double progress;

    public SkillResponse(Long id, String name, Double progress) {
        this.id = id;
        this.name = name;
        this.progress = progress;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getProgress() {
        return progress;
    }
}