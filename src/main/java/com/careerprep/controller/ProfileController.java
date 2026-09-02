package com.careerprep.controller;

import com.careerprep.dto.ProfileRequest;
import com.careerprep.dto.ProfileResponse;
import com.careerprep.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse createProfile(
            @Valid @RequestBody ProfileRequest request) {

        return profileService.createProfile(request);
    }


    @GetMapping
    public ProfileResponse getProfile() {
        return profileService.getProfile();
    }


    @PutMapping
    public ProfileResponse updateProfile(
            @Valid @RequestBody ProfileRequest request) {

        return profileService.updateProfile(request);
    }
}