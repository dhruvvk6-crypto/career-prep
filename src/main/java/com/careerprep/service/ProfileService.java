package com.careerprep.service;

import com.careerprep.dto.ProfileRequest;
import com.careerprep.dto.ProfileResponse;
import com.careerprep.entity.Profile;
import com.careerprep.entity.User;
import com.careerprep.exception.ApiException;
import com.careerprep.repository.ProfileRepository;
import com.careerprep.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(
            ProfileRepository profileRepository,
            UserRepository userRepository) {

        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public ProfileResponse createProfile(ProfileRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        if (profileRepository.existsByUserId(user.getId())) {
            throw ApiException.conflict("Profile already exists.");
        }

        Profile profile = new Profile();

        profile.setTargetRole(request.getTargetRole());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setBio(request.getBio());

        profile.setUser(user);

        Profile savedProfile = profileRepository.save(profile);

        return new ProfileResponse(
                savedProfile.getId(),
                savedProfile.getTargetRole(),
                savedProfile.getExperienceLevel(),
                savedProfile.getBio()
        );
    }
    public ProfileResponse getProfile() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        ApiException.notFound("Profile not found."));

        return new ProfileResponse(
                profile.getId(),
                profile.getTargetRole(),
                profile.getExperienceLevel(),
                profile.getBio()
        );
    }
    public ProfileResponse updateProfile(ProfileRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        ApiException.notFound("Profile not found."));

        profile.setTargetRole(request.getTargetRole());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setBio(request.getBio());

        Profile updatedProfile = profileRepository.save(profile);

        return new ProfileResponse(
                updatedProfile.getId(),
                updatedProfile.getTargetRole(),
                updatedProfile.getExperienceLevel(),
                updatedProfile.getBio()
        );
    }

}
