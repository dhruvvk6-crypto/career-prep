package com.careerprep.dto;

import java.util.List;

public class InterviewConfigurationResponse {

    private final List<String> roles;
    private final List<String> difficulties;

    public InterviewConfigurationResponse(List<String> roles, List<String> difficulties) {
        this.roles = roles;
        this.difficulties = difficulties;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getDifficulties() {
        return difficulties;
    }
}
