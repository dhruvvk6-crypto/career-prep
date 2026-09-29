package com.careerprep.service;

import com.careerprep.dto.LoginRequest;
import com.careerprep.dto.RegisterRequest;
import com.careerprep.dto.UserResponse;
import com.careerprep.entity.User;
import com.careerprep.exception.ApiException;
import com.careerprep.repository.UserRepository;
import com.careerprep.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserService userService;

    @Test
    void registerNormalizesEmailAndHashesPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setName("  Ada Lovelace  ");
        request.setEmail("Ada@Example.COM");
        request.setPassword("safePassword123");
        when(userRepository.existsByEmail("ada@example.com")).thenReturn(false);
        when(passwordEncoder.encode("safePassword123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.register(request);

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertThat(savedUser.getValue().getName()).isEqualTo("Ada Lovelace");
        assertThat(savedUser.getValue().getEmail()).isEqualTo("ada@example.com");
        assertThat(savedUser.getValue().getPassword()).isEqualTo("hashed-password");
        assertThat(savedUser.getValue().getRole()).isEqualTo("USER");
        assertThat(response.getEmail()).isEqualTo("ada@example.com");
    }

    @Test
    void registerRejectsAnExistingEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("ada@example.com");
        when(userRepository.existsByEmail("ada@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(ApiException.class)
                .hasMessage("An account with this email already exists.");
    }

    @Test
    void loginReturnsJwtForValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("Ada@Example.com");
        request.setPassword("safePassword123");
        User user = new User();
        user.setEmail("ada@example.com");
        user.setPassword("hashed-password");
        when(userRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("safePassword123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken("ada@example.com")).thenReturn("signed-jwt");

        assertThat(userService.login(request).getToken()).isEqualTo("signed-jwt");
    }
}
