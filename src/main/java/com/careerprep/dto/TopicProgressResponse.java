package com.careerprep.dto;

public class TopicProgressResponse {
    private final String path;
    private final String topic;
    private final long completed;
    private final long total;
    private final double progress;
    private final double averageScore;

    public TopicProgressResponse(String path, String topic, long completed, long total, double progress, double averageScore) {
        this.path = path;
        this.topic = topic;
        this.completed = completed;
        this.total = total;
        this.progress = progress;
        this.averageScore = averageScore;
    }

    public String getPath() { return path; }
    public String getTopic() { return topic; }
    public long getCompleted() { return completed; }
    public long getTotal() { return total; }
    public double getProgress() { return progress; }
    public double getAverageScore() { return averageScore; }
}
