package com.careerprep.controller;

import com.careerprep.dto.*;
import com.careerprep.service.InterviewScoringService;
import com.careerprep.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;
    private final InterviewScoringService interviewScoringService;



    public InterviewController(
            InterviewService interviewService,
            InterviewScoringService interviewScoringService) {

        this.interviewService = interviewService;
        this.interviewScoringService = interviewScoringService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewResponse createInterview(
            @Valid @RequestBody InterviewRequest request) {

        return interviewService.createInterview(request);
    }


    @GetMapping("/{id}")
    public InterviewResponse getInterview(@PathVariable Long id) {
        return interviewService.getInterview(id);
    }

    @PostMapping("/{interviewId}/answers")
    public InterviewAnswerResponse submitAnswer(
            @PathVariable Long interviewId,
            @RequestBody InterviewAnswerRequest request) {

        return interviewService.submitAnswer(interviewId, request);
    }


    @GetMapping("/{interviewId}/answers")
    public List<InterviewAnswerResponse> getAnswers(
            @PathVariable Long interviewId) {

        return interviewService.getAnswers(interviewId);
    }

    @PutMapping("/{interviewId}/answers/{answerId}")
    public InterviewAnswerResponse updateAnswer(
            @PathVariable Long interviewId,
            @PathVariable Long answerId,
            @RequestBody InterviewAnswerUpdateRequest request) {

        return interviewService.updateAnswer(
                interviewId,
                answerId,
                request
        );
    }


    @PutMapping("/{id}/complete")
    public InterviewResponse completeInterview(@PathVariable Long id) {

        return interviewService.completeInterview(id);
    }

    @GetMapping("/{id}/result")
    public InterviewResultResponse getInterviewResult(
            @PathVariable Long id) {

        return interviewScoringService.generateResult(id);
    }
}