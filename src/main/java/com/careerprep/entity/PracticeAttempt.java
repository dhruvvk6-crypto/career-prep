package com.careerprep.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "practice_attempts", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "challenge_id"}))
public class PracticeAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer = "";

    @Column(nullable = false, length = 20)
    private String status;

    private Double score;

    @Column(nullable = false)
    private Instant updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "challenge_id", nullable = false)
    private PracticeChallenge challenge;

    @PrePersist
    @PreUpdate
    void updateTimestamp() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Instant getUpdatedAt() { return updatedAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public PracticeChallenge getChallenge() { return challenge; }
    public void setChallenge(PracticeChallenge challenge) { this.challenge = challenge; }
}
