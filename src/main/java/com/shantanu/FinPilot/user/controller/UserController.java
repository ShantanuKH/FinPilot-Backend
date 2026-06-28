package com.shantanu.FinPilot.user.controller;


import com.shantanu.FinPilot.user.dto.UpdateProfileRequest;
import com.shantanu.FinPilot.user.dto.UserProfileResponse;
import com.shantanu.FinPilot.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileResponse getCurrentUserProfile(
            Authentication authentication
    ){
        String email = authentication.getName();
        return userService.getCurrentUserProfile(email);
    }

    @PostMapping("/updateProfile")
    public UserProfileResponse updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest updateProfileRequest

            ){
        String email = authentication.getName();

        return userService.updateProfile(
                email,
                updateProfileRequest
        );
    }

}
