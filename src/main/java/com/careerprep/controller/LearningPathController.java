package com.careerprep.controller;

import com.careerprep.dto.LearningPathResponse;
import com.careerprep.service.LearningPathService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/learning-paths")
public class LearningPathController {

    private final LearningPathService learningPathService;

    public LearningPathController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }

    @GetMapping
    public List<LearningPathResponse> getLearningPaths() {
        return learningPathService.getLearningPaths();
    }
}
