package com.careerprep.dto;

public class PracticeChallengeResponse {
    private final Long id;
    private final String path;
    private final String topic;
    private final String title;
    private final String prompt;
    private final String language;
    private final String challengeType;
    private final String starterCode;
    private final String answer;
    private final boolean completed;
    private final Double score;

    public PracticeChallengeResponse(Long id, String path, String topic, String title, String prompt,
                                     String language, String challengeType, String starterCode,
                                     String answer, boolean completed, Double score) {
        this.id = id;
        this.path = path;
        this.topic = topic;
        this.title = title;
        this.prompt = prompt;
        this.language = language;
        this.challengeType = challengeType;
        this.starterCode = starterCode;
        this.answer = answer;
        this.completed = completed;
        this.score = score;
    }

    public Long getId() { return id; }
    public String getPath() { return path; }
    public String getTopic() { return topic; }
    public String getTitle() { return title; }
    public String getPrompt() { return prompt; }
    public String getLanguage() { return language; }
    public String getChallengeType() { return challengeType; }
    public String getStarterCode() { return starterCode; }
    public String getAnswer() { return answer; }
    public boolean isCompleted() { return completed; }
    public Double getScore() { return score; }
}
