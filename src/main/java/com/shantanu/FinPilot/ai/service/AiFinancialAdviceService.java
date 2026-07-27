package com.shantanu.FinPilot.ai.service;

import com.shantanu.FinPilot.ai.dto.AiAdviceResponse;
import com.shantanu.FinPilot.ai.dto.AiFinancialContext;
import com.shantanu.FinPilot.ai.prompt.FinancialAdvicePromptBuilder;
import com.shantanu.FinPilot.recommendation.service.RecommendationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiFinancialAdviceService {

    private final ChatClient chatClient;
    private final FinancialAdvicePromptBuilder financialAdvicePromptBuilder;
    private final RecommendationService recommendationService;

    public AiFinancialAdviceService(
            ChatClient.Builder chatClientBuilder,
            FinancialAdvicePromptBuilder financialAdvicePromptBuilder,
            RecommendationService recommendationService) {

        this.chatClient = chatClientBuilder.build();
        this.financialAdvicePromptBuilder = financialAdvicePromptBuilder;
        this.recommendationService = recommendationService;
    }

    private AiAdviceResponse askAi(String prompt) {
        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(AiAdviceResponse.class);
    }

    public AiAdviceResponse generateFinancialAdvice(String email) {

        AiFinancialContext context =
                recommendationService.buildFinancialContext(email);

        String prompt =
                financialAdvicePromptBuilder.buildFinancialAdvicePrompt(context);

        return askAi(prompt);
    }
}