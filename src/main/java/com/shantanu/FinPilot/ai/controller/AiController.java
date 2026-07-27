package com.shantanu.FinPilot.ai.controller;

import com.shantanu.FinPilot.ai.dto.AiAdviceResponse;
import com.shantanu.FinPilot.ai.dto.chat.AiChatRequest;
import com.shantanu.FinPilot.ai.dto.chat.AiChatResponse;
import com.shantanu.FinPilot.ai.service.AiChatService;
import com.shantanu.FinPilot.ai.service.AiFinancialAdviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiFinancialAdviceService aiFinancialAdviceService;
    private final AiChatService aiChatService;
//    @GetMapping("/test")
//    public ResponseEntity<AiAdviceResponse> testAi(
//            @RequestParam String prompt
//    ) {
//
//        return ResponseEntity.ok(
//                aiService.askAi(prompt)
//        );
//    }

    @GetMapping("/advice")
    public ResponseEntity<AiAdviceResponse> getFinancialAdvice(Authentication authentication) {

        return ResponseEntity.ok(
                aiFinancialAdviceService.generateFinancialAdvice(authentication.getName())
        );
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chatService(
            Authentication authentication, @RequestBody AiChatRequest aiChatRequest) {

        return ResponseEntity.ok(
                aiChatService.chat(
                        authentication.getName(),
                        aiChatRequest
                )
        );
    }


}