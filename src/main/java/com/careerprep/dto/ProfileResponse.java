package com.careerprep.dto;

public class ProfileResponse {

    private Long id;
    private String targetRole;
    private String experienceLevel;
    private String bio;

    public ProfileResponse(
            Long id,
            String targetRole,
            String experienceLevel,
            String bio) {

        this.id = id;
        this.targetRole = targetRole;
        this.experienceLevel = experienceLevel;
        this.bio = bio;
    }

    public Long getId() {
        return id;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public String getBio() {
        return bio;
    }
}