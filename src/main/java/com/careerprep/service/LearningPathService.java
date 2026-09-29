package com.careerprep.service;

import com.careerprep.dto.LearningPathResponse;
import com.careerprep.dto.LearningTopicResponse;
import com.careerprep.entity.LearningPath;
import com.careerprep.repository.LearningPathRepository;
import com.careerprep.repository.LearningTopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final LearningTopicRepository learningTopicRepository;

    public LearningPathService(
            LearningPathRepository learningPathRepository,
            LearningTopicRepository learningTopicRepository) {
        this.learningPathRepository = learningPathRepository;
        this.learningTopicRepository = learningTopicRepository;
    }

    public List<LearningPathResponse> getLearningPaths() {
        return learningPathRepository.findAllByOrderByName().stream()
                .map(this::toResponse)
                .toList();
    }

    private LearningPathResponse toResponse(LearningPath path) {
        List<LearningTopicResponse> topics = learningTopicRepository
                .findByLearningPathIdOrderByDisplayOrder(path.getId())
                .stream()
                .map(topic -> new LearningTopicResponse(topic.getName(), topic.getDescription()))
                .toList();
        return new LearningPathResponse(
                path.getId(), path.getTargetRole(), path.getName(), path.getDescription(),
                path.getTechnology().getName(), path.getTechnology().getAccentColor(), topics);
    }
}
