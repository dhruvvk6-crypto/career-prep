package com.careerprep.config;

import com.careerprep.entity.LearningPath;
import com.careerprep.entity.LearningTopic;
import com.careerprep.entity.PracticeChallenge;
import com.careerprep.repository.LearningPathRepository;
import com.careerprep.repository.LearningTopicRepository;
import com.careerprep.repository.PracticeChallengeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class PracticeDataInitializer {

    @Bean
    @Order(2)
    CommandLineRunner seedPracticeChallenges(
            PracticeChallengeRepository challengeRepository,
            LearningPathRepository pathRepository,
            LearningTopicRepository topicRepository) {
        return args -> {
            if (challengeRepository.count() > 0) return;
            add(challengeRepository, pathRepository, topicRepository, "Java Backend Developer", "Core Java",
                    "Implement an LRU cache", "Design an LRU cache API. Explain how HashMap and a doubly linked list work together.",
                    "Java", "CODING", "class LruCache {\n  // add get and put methods\n}", "hashmap,linkedlist,cache,get,put", 1);
            add(challengeRepository, pathRepository, topicRepository, "Java Backend Developer", "Spring Boot",
                    "Secure a REST endpoint", "Show how you would protect a Spring Boot REST endpoint and explain the request flow.",
                    "Java", "ARCHITECTURE", "@RestController\nclass ProjectController {\n  // secure this endpoint\n}", "security,jwt,authentication,authorization,filter", 2);
            add(challengeRepository, pathRepository, topicRepository, "Java Backend Developer", "Backend Engineering",
                    "Design reliable retries", "Design a retry strategy for a payment API. Explain idempotency, backoff, and observability.",
                    "Java", "SYSTEM_DESIGN", "// Outline retry and idempotency handling", "retry,idempotent,backoff,log,metric", 3);
            add(challengeRepository, pathRepository, topicRepository, "Python Developer", "Python Fundamentals",
                    "Stream a large file", "Write a Python approach for processing a large log file without loading it entirely into memory.",
                    "Python", "CODING", "def process_log(path):\n    pass", "generator,yield,file,memory,iterate", 1);
            add(challengeRepository, pathRepository, topicRepository, "Python Developer", "Web APIs",
                    "Validate a FastAPI request", "Sketch a FastAPI endpoint that validates a new user request and returns useful errors.",
                    "Python", "CODING", "@app.post('/users')\ndef create_user(...):\n    pass", "fastapi,pydantic,validation,request,error", 2);
            add(challengeRepository, pathRepository, topicRepository, "Python Developer", "Data & Testing",
                    "Test an API service", "Write a test plan or pytest example for a Python API endpoint that creates an order.",
                    "Python", "TESTING", "def test_create_order():\n    pass", "pytest,test,request,response,mock", 3);
            add(challengeRepository, pathRepository, topicRepository, "Frontend Developer", "JavaScript",
                    "Build debounced search", "Implement a debounced search input and explain how you avoid outdated responses overwriting new ones.",
                    "JavaScript", "CODING", "function search(query) {\n  // add debounce and cancellation\n}", "debounce,request,cancel,async,race", 1);
            add(challengeRepository, pathRepository, topicRepository, "Frontend Developer", "React",
                    "Design a reusable form field", "Create a reusable React form field with validation and accessible labels.",
                    "JavaScript", "CODING", "function FormField({ label, value, onChange }) {\n  return null;\n}", "react,label,input,validation,accessibility", 2);
            add(challengeRepository, pathRepository, topicRepository, "Frontend Developer", "Frontend Quality",
                    "Audit a page for accessibility", "List the concrete changes you would make to improve keyboard access, semantic structure, and loading performance.",
                    "JavaScript", "QUALITY_REVIEW", "// Document your accessibility and performance improvements", "semantic,keyboard,accessibility,performance,loading", 3);
            add(challengeRepository, pathRepository, topicRepository, "SQL Developer", "SQL Queries",
                    "Find top customers", "Write SQL to return the top five customers by total completed order value.",
                    "SQL", "QUERY", "SELECT\n  -- customer totals\nFROM orders", "select,join,group,sum,order", 1);
            add(challengeRepository, pathRepository, topicRepository, "SQL Developer", "Database Performance",
                    "Improve a slow query", "A query filters orders by user and date. Describe the index and query-plan checks you would make.",
                    "SQL", "PERFORMANCE", "EXPLAIN SELECT * FROM orders\nWHERE user_id = ? AND created_at >= ?;", "index,explain,query,plan,filter", 2);
            add(challengeRepository, pathRepository, topicRepository, "SQL Developer", "Data Modeling",
                    "Model a booking system", "Propose tables and relationships for a booking platform with users, properties, reservations, and payments.",
                    "SQL", "DATA_MODELING", "CREATE TABLE users (\n  id BIGINT PRIMARY KEY\n);", "table,foreign,key,relationship,normalization", 3);
        };
    }

    private void add(PracticeChallengeRepository repository, LearningPathRepository paths,
                     LearningTopicRepository topics, String role, String topicName, String title,
                     String prompt, String language, String type, String starterCode, String keywords, int order) {
        LearningPath path = paths.findByTargetRole(role).orElseThrow();
        LearningTopic topic = topics.findByLearningPathTargetRoleAndName(role, topicName).orElseThrow();
        PracticeChallenge challenge = new PracticeChallenge();
        challenge.setLearningPath(path);
        challenge.setLearningTopic(topic);
        challenge.setTitle(title);
        challenge.setPrompt(prompt);
        challenge.setLanguage(language);
        challenge.setChallengeType(type);
        challenge.setStarterCode(starterCode);
        challenge.setEvaluationKeywords(keywords);
        challenge.setDisplayOrder(order);
        repository.save(challenge);
    }
}
