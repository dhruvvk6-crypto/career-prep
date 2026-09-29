package com.careerprep.config;

import com.careerprep.entity.*;
import com.careerprep.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class QuestionBankDataInitializer {

    @Bean
    @Order(1)
    CommandLineRunner seedQuestionBank(
            TechnologyRepository technologyRepository,
            LearningPathRepository learningPathRepository,
            LearningTopicRepository learningTopicRepository,
            QuestionBankQuestionRepository questionBankQuestionRepository) {
        return args -> {
            if (learningPathRepository.count() > 0) {
                return;
            }

            LearningPath javaPath = path(technologyRepository, learningPathRepository,
                    "Java", "#f59e0b", "Java Backend Developer", "Java & Spring Boot Backend",
                    "Build production-ready Java services, APIs, and data layers.");
            Map<String, LearningTopic> javaTopics = topics(learningTopicRepository, javaPath,
                    "Core Java", "Collections, concurrency, JVM, and object-oriented design.",
                    "Spring Boot", "Dependency injection, REST APIs, and transactions.",
                    "Backend Engineering", "Security, observability, and scalable service design.");

            LearningPath pythonPath = path(technologyRepository, learningPathRepository,
                    "Python", "#3776ab", "Python Developer", "Python Backend Developer",
                    "Create clean Python services with FastAPI, Django, and SQL.");
            Map<String, LearningTopic> pythonTopics = topics(learningTopicRepository, pythonPath,
                    "Python Fundamentals", "Data structures, functions, and Pythonic code.",
                    "Web APIs", "FastAPI/Django services, validation, and API design.",
                    "Data & Testing", "SQL access, testing, and reliable delivery.");

            LearningPath frontendPath = path(technologyRepository, learningPathRepository,
                    "JavaScript", "#eab308", "Frontend Developer", "JavaScript & React Frontend",
                    "Build accessible, performant user interfaces with JavaScript and React.");
            Map<String, LearningTopic> frontendTopics = topics(learningTopicRepository, frontendPath,
                    "JavaScript", "Modern language features, asynchronous code, and the browser.",
                    "React", "Components, state, effects, and reusable UI patterns.",
                    "Frontend Quality", "Accessibility, performance, tests, and delivery.");

            LearningPath sqlPath = path(technologyRepository, learningPathRepository,
                    "SQL", "#22c55e", "SQL Developer", "SQL & Data Foundations",
                    "Strengthen querying, data modeling, transactions, and performance skills.");
            Map<String, LearningTopic> sqlTopics = topics(learningTopicRepository, sqlPath,
                    "SQL Queries", "Filtering, joins, aggregates, and expressive queries.",
                    "Data Modeling", "Schema design, normalization, and relationships.",
                    "Database Performance", "Indexes, query plans, transactions, and scale.");

            seedJava(questionBankQuestionRepository, javaPath, javaTopics);
            seedPython(questionBankQuestionRepository, pythonPath, pythonTopics);
            seedFrontend(questionBankQuestionRepository, frontendPath, frontendTopics);
            seedSql(questionBankQuestionRepository, sqlPath, sqlTopics);
        };
    }

    private LearningPath path(TechnologyRepository technologies, LearningPathRepository paths,
                              String technologyName, String color, String role, String name, String description) {
        Technology technology = new Technology();
        technology.setName(technologyName);
        technology.setAccentColor(color);
        technology = technologies.save(technology);
        LearningPath path = new LearningPath();
        path.setTechnology(technology);
        path.setTargetRole(role);
        path.setName(name);
        path.setDescription(description);
        return paths.save(path);
    }

    private Map<String, LearningTopic> topics(LearningTopicRepository repository, LearningPath path,
                                               String nameOne, String descriptionOne,
                                               String nameTwo, String descriptionTwo,
                                               String nameThree, String descriptionThree) {
        Map<String, LearningTopic> topics = new LinkedHashMap<>();
        topics.put(nameOne, topic(repository, path, nameOne, descriptionOne, 1));
        topics.put(nameTwo, topic(repository, path, nameTwo, descriptionTwo, 2));
        topics.put(nameThree, topic(repository, path, nameThree, descriptionThree, 3));
        return topics;
    }

    private LearningTopic topic(LearningTopicRepository repository, LearningPath path,
                                String name, String description, int order) {
        LearningTopic topic = new LearningTopic();
        topic.setLearningPath(path);
        topic.setName(name);
        topic.setDescription(description);
        topic.setDisplayOrder(order);
        return repository.save(topic);
    }

    private void seedJava(QuestionBankQuestionRepository questions, LearningPath path, Map<String, LearningTopic> topics) {
        add(questions, path, topics.get("Core Java"), "BEGINNER", "JAVA", "Explain the difference between ArrayList and LinkedList.", "arraylist,linkedlist,access,insertion", 1);
        add(questions, path, topics.get("Spring Boot"), "BEGINNER", "SPRING", "What is dependency injection and why is it useful in Spring?", "dependency,injection,object,loose,test", 2);
        add(questions, path, topics.get("Backend Engineering"), "BEGINNER", "HTTP", "What makes an HTTP API RESTful?", "http,resource,method,status,stateless", 3);
        add(questions, path, topics.get("Core Java"), "INTERMEDIATE", "JAVA", "How do HashMap collisions work, and why are equals and hashCode important?", "hashmap,collision,equals,hashcode,bucket", 1);
        add(questions, path, topics.get("Spring Boot"), "INTERMEDIATE", "SPRING", "Describe how @Transactional affects a Spring service method.", "transaction,rollback,commit,database,proxy", 2);
        add(questions, path, topics.get("Backend Engineering"), "INTERMEDIATE", "SECURITY", "How should a backend validate and authorize an API request?", "validate,authenticate,authorize,token,permission", 3);
        add(questions, path, topics.get("Core Java"), "ADVANCED", "JAVA", "How would you design a thread-safe cache with eviction?", "thread,cache,concurrent,eviction,synchronization", 1);
        add(questions, path, topics.get("Spring Boot"), "ADVANCED", "SPRING", "How do propagation and isolation affect database transactions?", "propagation,isolation,transaction,concurrency,rollback", 2);
        add(questions, path, topics.get("Backend Engineering"), "ADVANCED", "SYSTEM_DESIGN", "Design a scalable notification service that handles retryable failures.", "queue,retry,worker,scale,delivery", 3);
    }

    private void seedPython(QuestionBankQuestionRepository questions, LearningPath path, Map<String, LearningTopic> topics) {
        add(questions, path, topics.get("Python Fundamentals"), "BEGINNER", "PYTHON", "What is the difference between a list and a tuple in Python?", "list,tuple,mutable,immutable,sequence", 1);
        add(questions, path, topics.get("Web APIs"), "BEGINNER", "PYTHON_API", "What problem does a Python virtual environment solve?", "environment,dependency,package,isolation,version", 2);
        add(questions, path, topics.get("Data & Testing"), "BEGINNER", "TESTING", "Why should a Python service have automated tests?", "test,regression,confidence,automation,quality", 3);
        add(questions, path, topics.get("Python Fundamentals"), "INTERMEDIATE", "PYTHON", "When would you use a generator instead of a list?", "generator,iterator,memory,yield,lazy", 1);
        add(questions, path, topics.get("Web APIs"), "INTERMEDIATE", "FASTAPI", "How would you validate request data in a FastAPI endpoint?", "validation,pydantic,request,schema,error", 2);
        add(questions, path, topics.get("Data & Testing"), "INTERMEDIATE", "PYTHON_DATA", "How would you prevent SQL injection in a Python application?", "parameter,query,sql,injection,validate", 3);
        add(questions, path, topics.get("Python Fundamentals"), "ADVANCED", "PYTHON", "How would you diagnose a slow Python service with CPU-bound work?", "profile,cpu,process,thread,async", 1);
        add(questions, path, topics.get("Web APIs"), "ADVANCED", "API_DESIGN", "How would you design idempotent payment processing in a Python API?", "idempotent,key,retry,request,duplicate", 2);
        add(questions, path, topics.get("Data & Testing"), "ADVANCED", "DELIVERY", "How would you make a Python background task reliable and observable?", "queue,retry,log,metric,worker", 3);
    }

    private void seedFrontend(QuestionBankQuestionRepository questions, LearningPath path, Map<String, LearningTopic> topics) {
        add(questions, path, topics.get("JavaScript"), "BEGINNER", "JAVASCRIPT", "Explain the difference between let, const, and var.", "let,const,var,scope,hoisting", 1);
        add(questions, path, topics.get("React"), "BEGINNER", "REACT", "What is a React component and why are props useful?", "component,props,reusable,data,ui", 2);
        add(questions, path, topics.get("Frontend Quality"), "BEGINNER", "ACCESSIBILITY", "Why should images have meaningful alt text?", "alt,screen,reader,accessibility,image", 3);
        add(questions, path, topics.get("JavaScript"), "INTERMEDIATE", "JAVASCRIPT", "How does async/await improve asynchronous JavaScript code?", "async,await,promise,error,asynchronous", 1);
        add(questions, path, topics.get("React"), "INTERMEDIATE", "REACT", "When should React state be lifted up to a parent component?", "state,parent,shared,props,component", 2);
        add(questions, path, topics.get("Frontend Quality"), "INTERMEDIATE", "PERFORMANCE", "How would you investigate a slow React page?", "profile,render,network,performance,memo", 3);
        add(questions, path, topics.get("JavaScript"), "ADVANCED", "JAVASCRIPT", "How would you prevent race conditions in a search-as-you-type interface?", "request,cancel,debounce,race,state", 1);
        add(questions, path, topics.get("React"), "ADVANCED", "REACT", "How would you organize state for a complex multi-step form?", "state,form,context,reducer,validation", 2);
        add(questions, path, topics.get("Frontend Quality"), "ADVANCED", "FRONTEND_ARCHITECTURE", "How would you improve the accessibility and resilience of a design system?", "accessibility,component,test,semantic,fallback", 3);
    }

    private void seedSql(QuestionBankQuestionRepository questions, LearningPath path, Map<String, LearningTopic> topics) {
        add(questions, path, topics.get("SQL Queries"), "BEGINNER", "SQL", "What is the difference between WHERE and HAVING?", "where,having,group,filter,aggregate", 1);
        add(questions, path, topics.get("SQL Queries"), "BEGINNER", "SQL", "Explain the difference between an INNER JOIN and a LEFT JOIN.", "inner,left,join,matching,null", 2);
        add(questions, path, topics.get("Data Modeling"), "BEGINNER", "DATA_MODELING", "What is normalization and why is it useful?", "normalization,duplicate,table,relationship,consistency", 3);
        add(questions, path, topics.get("SQL Queries"), "INTERMEDIATE", "SQL", "How would you investigate a slow SQL query?", "explain,index,query,plan,scan", 1);
        add(questions, path, topics.get("Data Modeling"), "INTERMEDIATE", "DATABASE", "How do ACID properties protect a transaction?", "atomic,consistent,isolation,durable,transaction", 2);
        add(questions, path, topics.get("Database Performance"), "INTERMEDIATE", "DATA_MODELING", "When might denormalization be appropriate?", "denormalization,read,performance,duplicate,tradeoff", 3);
        add(questions, path, topics.get("Database Performance"), "ADVANCED", "DATABASE", "How would you choose an isolation level for concurrent order updates?", "isolation,transaction,lock,concurrent,consistency", 1);
        add(questions, path, topics.get("Database Performance"), "ADVANCED", "SQL", "How would you optimize a query that joins several large tables?", "index,join,plan,filter,partition", 2);
        add(questions, path, topics.get("Data Modeling"), "ADVANCED", "DATABASE", "When would partitioning improve a database design?", "partition,table,range,query,scale", 3);
    }

    private void add(QuestionBankQuestionRepository repository, LearningPath path, LearningTopic topic,
                     String difficulty, String category, String question, String keywords, int order) {
        QuestionBankQuestion item = new QuestionBankQuestion();
        item.setLearningPath(path);
        item.setLearningTopic(topic);
        item.setDifficulty(difficulty);
        item.setCategory(category);
        item.setQuestion(question);
        item.setEvaluationKeywords(keywords);
        item.setDisplayOrder(order);
        repository.save(item);
    }
}
