package com.shantanu.FinPilot.profile.controller;

import com.shantanu.FinPilot.profile.dto.FinancialHealthResponse;
import com.shantanu.FinPilot.profile.dto.ProfileResponse;
import com.shantanu.FinPilot.profile.dto.UpdateProfileRequest;
import com.shantanu.FinPilot.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                profileService.getProfile(email)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest updateProfileRequest
            ){
        String email = authentication.getName();

        return ResponseEntity.ok(
                profileService.updateProfile(
                        email,
                        updateProfileRequest
                )
        );

    }

    @GetMapping("/financial-health")
    public ResponseEntity<FinancialHealthResponse>getFinancialHealth(
            Authentication authentication
    ){
            String email = authentication.getName();

            return ResponseEntity.ok(
                    profileService.getFinancialHealth(email)
            );
    }
}