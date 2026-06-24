package com.shantanu.FinPilot.auth.service;


import com.shantanu.FinPilot.auth.dto.AuthResponse;
import com.shantanu.FinPilot.auth.dto.LoginRequest;
import com.shantanu.FinPilot.auth.dto.RegisterRequest;
import com.shantanu.FinPilot.common.exception.InvalidCredentialsException;
import com.shantanu.FinPilot.common.exception.UserAlreadyExistsException;
import com.shantanu.FinPilot.common.security.JwtService;
import com.shantanu.FinPilot.user.entity.Role;
import com.shantanu.FinPilot.user.entity.RoleType;
import com.shantanu.FinPilot.user.entity.User;
import com.shantanu.FinPilot.user.repository.RoleRepository;
import com.shantanu.FinPilot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

//    User Register Service
    public void register(RegisterRequest registerRequest){

//        If user already exists
        if(userRepository.existsByEmail(
                registerRequest.getEmail())){
            throw new UserAlreadyExistsException(
                    "User already exists with email: " + registerRequest.getEmail()
            );
        }

//        If role is present
        Role role = roleRepository
                .findByName(RoleType.ROLE_USER)
                .orElseThrow(()->new RuntimeException("Default role not found"));

//        Password
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

//        Create User Entity to save in DB
        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(encodedPassword)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user.getRoles().add(role);
//           Save to DB
        userRepository.save(user);

    }

//    User LoginService
    public AuthResponse login(LoginRequest loginRequest) {

        User user = userRepository
                .findByEmail(loginRequest.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        boolean isPasswordValid = passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        );

        if (!isPasswordValid) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(
                user.getEmail()
        );

        return AuthResponse.builder()
                .message("Login Successful")
                .token(token)
                .build();
    }



}
