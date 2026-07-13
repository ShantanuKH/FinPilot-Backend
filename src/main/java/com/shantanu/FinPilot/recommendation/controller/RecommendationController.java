package com.shantanu.FinPilot.recommendation.controller;

import com.shantanu.FinPilot.recommendation.dto.RecommendationResponse;
import com.shantanu.FinPilot.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    
    @GetMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(
            Authentication authentication
    ){
        String email = authentication.getName();

        return ResponseEntity.ok(
                recommendationService.getRecommendations(email)
        );
    }
}