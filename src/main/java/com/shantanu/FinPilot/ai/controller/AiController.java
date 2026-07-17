package com.shantanu.FinPilot.ai.controller;

import com.shantanu.FinPilot.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @GetMapping("/test")
    public ResponseEntity<String> testAi(
            @RequestParam String prompt
    ) {

        return ResponseEntity.ok(
                aiService.askAi(prompt)
        );
    }

    @GetMapping("/advice")
    public ResponseEntity<String> getFinancialAdvice(Authentication authentication) {
        return ResponseEntity.ok(
                aiService.generateFinancialAdvice(authentication.getName())
        );
    }

}