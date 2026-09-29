package com.careerprep.controller;

import com.careerprep.dto.*;
import com.careerprep.service.PracticeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/practice")
public class PracticeController {

    private final PracticeService practiceService;
    public PracticeController(PracticeService practiceService) { this.practiceService = practiceService; }

    @GetMapping("/challenges")
    public List<PracticeChallengeResponse> getChallenges(@RequestParam(required = false) String path) {
        return practiceService.getChallenges(path);
    }

    @PutMapping("/challenges/{challengeId}/attempt")
    public PracticeAttemptResponse saveAttempt(@PathVariable Long challengeId,
                                               @Valid @RequestBody PracticeAttemptRequest request) {
        return practiceService.saveAttempt(challengeId, request);
    }

    @GetMapping("/insights")
    public PracticeInsightsResponse getInsights() { return practiceService.getInsights(); }
}
