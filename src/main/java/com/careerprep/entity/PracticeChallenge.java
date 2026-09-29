package com.careerprep.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "practice_challenges")
public class PracticeChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, length = 3000)
    private String prompt;

    @Column(nullable = false, length = 40)
    private String language;

    @Column(nullable = false, length = 30)
    private String challengeType;

    @Column(columnDefinition = "TEXT")
    private String starterCode;

    @Column(nullable = false, length = 1000)
    private String evaluationKeywords;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @ManyToOne(optional = false)
    @JoinColumn(name = "learning_topic_id", nullable = false)
    private LearningTopic learningTopic;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getChallengeType() { return challengeType; }
    public void setChallengeType(String challengeType) { this.challengeType = challengeType; }
    public String getStarterCode() { return starterCode; }
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }
    public String getEvaluationKeywords() { return evaluationKeywords; }
    public void setEvaluationKeywords(String evaluationKeywords) { this.evaluationKeywords = evaluationKeywords; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LearningPath getLearningPath() { return learningPath; }
    public void setLearningPath(LearningPath learningPath) { this.learningPath = learningPath; }
    public LearningTopic getLearningTopic() { return learningTopic; }
    public void setLearningTopic(LearningTopic learningTopic) { this.learningTopic = learningTopic; }
}
