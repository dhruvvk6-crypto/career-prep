package com.careerprep.dto;

import java.util.List;

public class LearningPathResponse {
    private final Long id;
    private final String targetRole;
    private final String name;
    private final String description;
    private final String technology;
    private final String accentColor;
    private final List<LearningTopicResponse> topics;

    public LearningPathResponse(
            Long id,
            String targetRole,
            String name,
            String description,
            String technology,
            String accentColor,
            List<LearningTopicResponse> topics) {
        this.id = id;
        this.targetRole = targetRole;
        this.name = name;
        this.description = description;
        this.technology = technology;
        this.accentColor = accentColor;
        this.topics = topics;
    }

    public Long getId() { return id; }
    public String getTargetRole() { return targetRole; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTechnology() { return technology; }
    public String getAccentColor() { return accentColor; }
    public List<LearningTopicResponse> getTopics() { return topics; }
}
