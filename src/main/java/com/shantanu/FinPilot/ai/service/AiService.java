package com.shantanu.FinPilot.ai.service;

import com.shantanu.FinPilot.recommendation.service.RecommendationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final RecommendationService recommendationService;

    public AiService(ChatClient.Builder chatClientBuilder, RecommendationService recommendationService) {
        this.chatClient = chatClientBuilder.build();
        this.recommendationService = recommendationService;
    }

    public String askAi(String prompt) {
        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }

    private String buildPrompt(List<String> recommendations) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
            You are an experienced personal financial advisor.

            Analyze the user's financial recommendations and provide
            practical, personalized advice.

            Financial Recommendations:
            """);

        for (String recommendation : recommendations) {
            prompt.append("- ")
                    .append(recommendation)
                    .append("\n");
        }

        prompt.append("""
            
            Instructions:
            - Explain the recommendations in simple language.
            - Suggest practical next steps.
            - Keep the response under 250 words.
            - Use bullet points.
            - Be encouraging and professional.
            - Do not make up financial information that was not provided.
            """);

        return prompt.toString();
    }

    public String generateFinancialAdvice(String email) {

        List<String> recommendations =
                recommendationService.generateRecommendations(email);

        String prompt = buildPrompt(recommendations);

        return askAi(prompt);
    }
}