package com.shantanu.FinPilot.ai.service;

import com.shantanu.FinPilot.ai.dto.AiAdviceResponse;
import com.shantanu.FinPilot.ai.dto.AiFinancialContext;
import com.shantanu.FinPilot.ai.prompt.FinancialAdvicePromptBuilder;
import com.shantanu.FinPilot.recommendation.service.RecommendationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiFinancialAdviceService {

    private final ChatClient chatClient;
    private final FinancialAdvicePromptBuilder financialAdvicePromptBuilder;
    private final RecommendationService recommendationService;

    public AiFinancialAdviceService(
            ChatClient.Builder chatClientBuilder,
            FinancialAdvicePromptBuilder financialAdvicePromptBuilder,
            RecommendationService recommendationService
    ) {
        this.chatClient = chatClientBuilder.build();
        this.financialAdvicePromptBuilder =
                financialAdvicePromptBuilder;
        this.recommendationService =
                recommendationService;
    }

    // =========================================================
    // AI REQUEST
    // =========================================================

    private AiAdviceResponse askAi(String prompt) {

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .entity(AiAdviceResponse.class);
    }

    // =========================================================
    // GENERATE FINANCIAL ADVICE
    // =========================================================

    public AiAdviceResponse generateFinancialAdvice(
            String email
    ) {

        /*
         * Build the user's financial context first.
         */
        AiFinancialContext context =
                recommendationService
                        .buildFinancialContext(email);

        /*
         * -----------------------------------------------------
         * FIRST-TIME USER / EMPTY FINANCIAL DATA
         * -----------------------------------------------------
         *
         * If the user has not started tracking finances yet,
         * there is no reason to send an empty context to Groq.
         *
         * Instead, return a meaningful onboarding response.
         */
        if (!hasFinancialData(context)) {
            return createWelcomeResponse();
        }

        /*
         * -----------------------------------------------------
         * EXISTING USER WITH FINANCIAL DATA
         * -----------------------------------------------------
         *
         * Build the AI prompt and generate the actual
         * personalized financial advice.
         */
        String prompt =
                financialAdvicePromptBuilder
                        .buildFinancialAdvicePrompt(context);

        return askAi(prompt);
    }

    // =========================================================
    // CHECK FINANCIAL DATA
    // =========================================================

    private boolean hasFinancialData(
            AiFinancialContext context
    ) {

        /*
         * A null context means there is no usable
         * financial information.
         */
        if (context == null) {
            return false;
        }

        /*
         * Check whether the user has actual expenses.
         */
        Double totalExpenses =
                context.getTotalExpenses();

        boolean hasExpenses =
                totalExpenses != null
                        && totalExpenses > 0;

        /*
         * Recommendations can also indicate that the
         * user has enough financial information available
         * for personalized advice.
         */
        boolean hasRecommendations =
                context.getRecommendations() != null
                        && !context.getRecommendations().isEmpty();

        /*
         * Generate AI advice if we have meaningful
         * financial information.
         */
        return hasExpenses || hasRecommendations;
    }

    // =========================================================
    // FIRST-TIME USER RESPONSE
    // =========================================================

    private AiAdviceResponse createWelcomeResponse() {

        return AiAdviceResponse.builder()

                .summary(
                        "Welcome to FinPilot AI! Your personal " +
                                "financial assistant is ready. Start by " +
                                "adding your expenses and financial details " +
                                "to unlock personalized insights."
                )

                .strengths(
                        List.of(
                                "You've taken the first step toward " +
                                        "managing your finances with FinPilot."
                        )
                )

                .improvements(
                        List.of(
                                "Start tracking your expenses so " +
                                        "FinPilot can understand your spending."
                        )
                )

                .actionItems(
                        List.of(
                                "Add your first expense.",
                                "Set up your monthly budget.",
                                "Add your investments when you're ready."
                        )
                )

                .motivation(
                        "Small financial decisions add up over time. " +
                                "Start tracking today and let FinPilot help " +
                                "you build better financial habits."
                )

                .build();
    }
}