package com.shantanu.FinPilot.ai.service;

import com.shantanu.FinPilot.ai.context.ContextDetector;
import com.shantanu.FinPilot.ai.context.ContextType;
import com.shantanu.FinPilot.ai.dto.AiFinancialContext;
import com.shantanu.FinPilot.ai.dto.chat.AiChatRequest;
import com.shantanu.FinPilot.ai.dto.chat.AiChatResponse;
import com.shantanu.FinPilot.ai.prompt.FinancialChatPromptBuilder;
import com.shantanu.FinPilot.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiChatService {

    private final ChatClient.Builder chatClientBuilder;
    private final RecommendationService recommendationService;
    private final FinancialChatPromptBuilder financialChatPromptBuilder;
    private final ContextDetector contextDetector;

    private AiChatResponse askAi(String prompt) {

        ChatClient chatClient = chatClientBuilder.build();

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(AiChatResponse.class);
    }

    public AiChatResponse chat(String email, AiChatRequest request) {

        ContextType contextType =
                contextDetector.detect(request.getMessage());

        AiFinancialContext context =
                recommendationService.buildFinancialContext(email);

        String prompt =
                financialChatPromptBuilder.buildChatPrompt(
                        context,
                        request.getMessage(),
                        contextType
                );

        return askAi(prompt);
    }
}