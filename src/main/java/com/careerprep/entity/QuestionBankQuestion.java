package com.careerprep.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question_bank_questions")
public class QuestionBankQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 2000)
    private String question;

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
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
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
