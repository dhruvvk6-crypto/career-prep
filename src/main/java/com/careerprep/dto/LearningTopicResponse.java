package com.careerprep.dto;

public class LearningTopicResponse {
    private final String name;
    private final String description;

    public LearningTopicResponse(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}
