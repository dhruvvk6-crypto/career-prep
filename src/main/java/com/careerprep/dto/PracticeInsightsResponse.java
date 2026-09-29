package com.careerprep.dto;

import java.util.List;

public class PracticeInsightsResponse {
    private final String recommendedNextPractice;
    private final List<PathReadinessResponse> pathReadiness;
    private final List<TopicProgressResponse> topicProgress;

    public PracticeInsightsResponse(String recommendedNextPractice,
                                    List<PathReadinessResponse> pathReadiness,
                                    List<TopicProgressResponse> topicProgress) {
        this.recommendedNextPractice = recommendedNextPractice;
        this.pathReadiness = pathReadiness;
        this.topicProgress = topicProgress;
    }

    public String getRecommendedNextPractice() { return recommendedNextPractice; }
    public List<PathReadinessResponse> getPathReadiness() { return pathReadiness; }
    public List<TopicProgressResponse> getTopicProgress() { return topicProgress; }
}
