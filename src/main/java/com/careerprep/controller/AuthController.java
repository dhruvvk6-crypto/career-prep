package com.careerprep.controller;

import com.careerprep.dto.LoginRequest;
import com.careerprep.dto.LoginResponse;
import com.careerprep.dto.RegisterRequest;
import com.careerprep.dto.UserResponse;
import com.careerprep.entity.User;
import com.careerprep.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return userService.login(request);
    }
}