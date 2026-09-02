package com.careerprep.controller;

import com.careerprep.dto.SkillProgressUpdateRequest;
import com.careerprep.dto.SkillRequest;
import com.careerprep.dto.SkillResponse;
import com.careerprep.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillResponse addSkill(
            @Valid @RequestBody SkillRequest request) {

        return skillService.addSkill(request);
    }

    @GetMapping
    public List<SkillResponse> getSkills() {
        return skillService.getSkills();
    }

    @DeleteMapping("/{skillId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSkill(@PathVariable Long skillId) {

        skillService.deleteSkill(skillId);
    }

    @PatchMapping("/{skillId}/progress")
    public SkillResponse updateProgress(
            @PathVariable Long skillId,
            @Valid @RequestBody SkillProgressUpdateRequest request) {

        return skillService.updateProgress(skillId, request);
    }
}