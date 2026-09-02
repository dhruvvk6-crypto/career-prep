package com.careerprep.service;

import com.careerprep.dto.*;
import com.careerprep.entity.InterviewQuestion;
import com.careerprep.entity.InterviewSession;
import com.careerprep.entity.User;
import com.careerprep.enums.InterviewStatus;
import com.careerprep.repository.InterviewAnswerRepository;
import com.careerprep.repository.InterviewQuestionRepository;
import com.careerprep.repository.InterviewSessionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.careerprep.entity.InterviewAnswer;


import java.util.ArrayList;
import java.util.List;

@Service
public class InterviewService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final InterviewAnswerRepository interviewAnswerRepository;

    public InterviewService(
            InterviewSessionRepository interviewSessionRepository,
            InterviewQuestionRepository interviewQuestionRepository,
            InterviewAnswerRepository interviewAnswerRepository) {

        this.interviewSessionRepository = interviewSessionRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.interviewAnswerRepository = interviewAnswerRepository;
    }

    public InterviewResponse createInterview(InterviewRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        InterviewSession session = new InterviewSession();

        session.setTargetRole(request.getTargetRole());
        session.setStatus(InterviewStatus.IN_PROGRESS);
        session.setUser(user);

        InterviewSession savedSession =
                interviewSessionRepository.save(session);

        List<InterviewQuestion> questions =
                generateQuestions(savedSession);

        List<InterviewQuestion> savedQuestions =
                interviewQuestionRepository.saveAll(questions);

        return buildInterviewResponse(
                savedSession,
                savedQuestions
        );
    }

    private List<InterviewQuestion> generateQuestions(
            InterviewSession session) {

        List<InterviewQuestion> questions =
                new ArrayList<>();

        InterviewQuestion question1 = new InterviewQuestion();
        question1.setQuestion(
                "Explain the difference between ArrayList and LinkedList in Java."
        );
        question1.setQuestionType("TECHNICAL");
        question1.setQuestionOrder(1);
        question1.setInterviewSession(session);
        questions.add(question1);

        InterviewQuestion question2 = new InterviewQuestion();
        question2.setQuestion(
                "What is dependency injection in Spring Boot?"
        );
        question2.setQuestionType("TECHNICAL");
        question2.setQuestionOrder(2);
        question2.setInterviewSession(session);
        questions.add(question2);

        InterviewQuestion question3 = new InterviewQuestion();
        question3.setQuestion(
                "Explain how JWT authentication works."
        );
        question3.setQuestionType("TECHNICAL");
        question3.setQuestionOrder(3);
        question3.setInterviewSession(session);
        questions.add(question3);

        return questions;
    }

    private InterviewResponse buildInterviewResponse(
            InterviewSession session,
            List<InterviewQuestion> questions) {

        List<InterviewQuestionResponse> questionResponses =
                questions.stream()
                        .map(question ->
                                new InterviewQuestionResponse(
                                        question.getId(),
                                        question.getQuestion(),
                                        question.getQuestionType(),
                                        question.getQuestionOrder()
                                )
                        )
                        .toList();

        return new InterviewResponse(
                session.getId(),
                session.getTargetRole(),
                session.getStatus().name(),
                questionResponses
        );
    }



    public InterviewResponse getInterview(Long id) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        InterviewSession interview =
                interviewSessionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Interview not found"));

        if (!interview.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this interview");
        }

        return convertToResponse(interview);
    }


    private InterviewResponse convertToResponse(InterviewSession session) {

        List<InterviewQuestion> questions =
                interviewQuestionRepository
                        .findByInterviewSessionIdOrderByQuestionOrder(session.getId());

        List<InterviewQuestionResponse> questionResponses =
                questions.stream()
                        .map(question -> new InterviewQuestionResponse(
                                question.getId(),
                                question.getQuestion(),
                                question.getQuestionType(),
                                question.getQuestionOrder()
                        ))
                        .toList();

        return new InterviewResponse(
                session.getId(),
                session.getTargetRole(),
                session.getStatus().name(),
                questionResponses
        );
    }



    public InterviewAnswerResponse submitAnswer(
            Long interviewId,
            InterviewAnswerRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        InterviewSession interview =
                interviewSessionRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new RuntimeException("Interview not found"));

        if (!interview.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to access this interview");
        }

        InterviewQuestion question =
                interviewQuestionRepository.findById(request.getQuestionId())
                        .orElseThrow(() ->
                                new RuntimeException("Question not found"));

        if (!question.getInterviewSession().getId().equals(interviewId)) {
            throw new RuntimeException(
                    "Question does not belong to this interview"
            );
        }

        InterviewAnswer answer = new InterviewAnswer();

        answer.setInterviewSession(interview);
        answer.setInterviewQuestion(question);
        answer.setAnswer(request.getAnswer());

        InterviewAnswer savedAnswer =
                interviewAnswerRepository.save(answer);

        return new InterviewAnswerResponse(
                savedAnswer.getId(),
                question.getId(),
                savedAnswer.getAnswer()
        );
    }



    public List<InterviewAnswerResponse> getAnswers(Long interviewId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        InterviewSession interview =
                interviewSessionRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new RuntimeException("Interview not found"));

        if (!interview.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this interview");
        }

        List<InterviewAnswer> answers =
                interviewAnswerRepository
                        .findByInterviewSessionId(interviewId);

        return answers.stream()
                .map(answer ->
                        new InterviewAnswerResponse(
                                answer.getId(),
                                answer.getInterviewQuestion().getId(),
                                answer.getAnswer()
                        ))
                .toList();
    }



    public InterviewAnswerResponse updateAnswer(
            Long interviewId,
            Long answerId,
            InterviewAnswerUpdateRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        InterviewSession interview =
                interviewSessionRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new RuntimeException("Interview not found"));

        if (!interview.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this interview");
        }

        InterviewAnswer answer =
                interviewAnswerRepository.findById(answerId)
                        .orElseThrow(() ->
                                new RuntimeException("Answer not found"));

        if (!answer.getInterviewSession().getId().equals(interviewId)) {
            throw new RuntimeException(
                    "Answer does not belong to this interview");
        }

        answer.setAnswer(request.getAnswer());

        InterviewAnswer updatedAnswer =
                interviewAnswerRepository.save(answer);

        return new InterviewAnswerResponse(
                updatedAnswer.getId(),
                updatedAnswer.getInterviewQuestion().getId(),
                updatedAnswer.getAnswer()
        );
    }


    public InterviewResponse completeInterview(Long id) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        InterviewSession interview =
                interviewSessionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Interview not found"));



        if (!interview.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this interview");
        }

        interview.setStatus(InterviewStatus.COMPLETED);

        InterviewSession completedInterview =
                interviewSessionRepository.save(interview);

        return convertToResponse(completedInterview);
    }
}