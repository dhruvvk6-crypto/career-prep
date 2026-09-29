package com.careerprep.service;

import com.careerprep.dto.LoginRequest;
import com.careerprep.dto.LoginResponse;
import com.careerprep.dto.RegisterRequest;
import com.careerprep.dto.UserResponse;
import com.careerprep.entity.User;
import com.careerprep.exception.ApiException;
import com.careerprep.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.careerprep.security.JwtService;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(normalizeEmail(request.getEmail()))
                .orElseThrow(() ->
                        ApiException.unauthorized("Invalid email or password."));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw ApiException.unauthorized("Invalid email or password.");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token);
    }

    public UserResponse register(RegisterRequest request) {

        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw ApiException.conflict("An account with this email already exists.");
        }

        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole("USER");

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
