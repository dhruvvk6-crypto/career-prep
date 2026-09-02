package com.careerprep.service;

import com.careerprep.dto.SkillProgressUpdateRequest;
import com.careerprep.dto.SkillRequest;
import com.careerprep.dto.SkillResponse;
import com.careerprep.entity.Skill;
import com.careerprep.entity.User;
import com.careerprep.repository.SkillRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }



    public SkillResponse addSkill(SkillRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        if (skillRepository.existsByUserIdAndName(
                user.getId(),
                request.getName())) {

            throw new RuntimeException("Skill already exists");
        }

        Skill skill = new Skill();

        skill.setName(request.getName());
        skill.setUser(user);

        Skill savedSkill = skillRepository.save(skill);

        return new SkillResponse(
                savedSkill.getId(),
                savedSkill.getName(),
                savedSkill.getProgress()
        );
    }


    public List<SkillResponse> getSkills() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        List<Skill> skills =
                skillRepository.findByUserId(user.getId());

        return skills.stream()
                .map(skill ->
                        new SkillResponse(
                                skill.getId(),
                                skill.getName(),
                                skill.getProgress()
                        )
                )
                .toList();
    }


    public void deleteSkill(Long skillId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        Skill skill = skillRepository
                .findByIdAndUserId(skillId, user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Skill not found"));

        skillRepository.delete(skill);
    }

    public SkillResponse updateProgress(
            Long skillId,
            SkillProgressUpdateRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        Skill skill = skillRepository
                .findByIdAndUserId(skillId, user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Skill not found"));

        if (request.getProgress() == null ||
                request.getProgress() < 0 ||
                request.getProgress() > 100) {

            throw new RuntimeException(
                    "Progress must be between 0 and 100");
        }

        skill.setProgress(request.getProgress());

        Skill savedSkill = skillRepository.save(skill);

        return new SkillResponse(
                savedSkill.getId(),
                savedSkill.getName(),
                savedSkill.getProgress()
        );
    }
}