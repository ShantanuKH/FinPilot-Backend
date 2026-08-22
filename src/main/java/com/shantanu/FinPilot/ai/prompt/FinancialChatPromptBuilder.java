package com.shantanu.FinPilot.ai.prompt;

import com.shantanu.FinPilot.ai.context.ContextType;
import com.shantanu.FinPilot.ai.dto.AiFinancialContext;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class FinancialChatPromptBuilder {

    /**
     * Builds the prompt for AI chat by combining the user's
     * financial context with their current question.
     *
     * @param context User's financial context.
     * @param userMessage User's question.
     * @param contextType Detected financial context.
     * @return Prompt sent to the AI model.
     */
    public String buildChatPrompt(
            AiFinancialContext context,
            String userMessage,
            ContextType contextType
    ) {

        String relevantContext =
                buildRelevantContext(context, contextType);

        return """
                You are FinPilot AI, an intelligent and practical personal financial advisor.

                Your goal is to answer the user's financial questions using the
                financial information provided by the FinPilot application.

                Follow these rules carefully:

                - Use ONLY the financial information provided below.
                - Never invent or estimate user-specific financial information.
                - Never contradict the provided financial information.
                - If some required financial data is unavailable, clearly state that.
                - Never guarantee investment returns or financial outcomes.
                - Do not provide illegal, unethical or fraudulent financial advice.
                - Use the existing financial recommendations as your primary source of personalised advice.
                - If expanding on a recommendation, explain why it is important.
                - Clearly distinguish between personalised advice and general financial education.
                - Keep your response clear, concise, practical and easy to understand.
                - Explain your reasoning briefly.
                - Suggest actionable next steps whenever appropriate.
                - If the user's question is unrelated to personal finance,
                  politely explain that you specialise in budgeting, expenses,
                  savings, investments, income and financial planning,
                  then encourage the user to ask a finance-related question.

                Financial Information
                ---------------------
                %s

                User Question
                -------------
                %s
                """.formatted(
                relevantContext,
                userMessage
        );
    }

    /**
     * Builds only the financial information relevant to
     * the detected context to reduce prompt size.
     *
     * @param context User's financial context.
     * @param contextType Detected financial context.
     * @return Relevant financial information.
     */
    private String buildRelevantContext(
            AiFinancialContext context,
            ContextType contextType
    ) {

        String recommendations =
                context.getRecommendations()
                        .stream()
                        .map(recommendation -> "- " + recommendation)
                        .collect(Collectors.joining("\n"));

        return switch (contextType) {

            case EXPENSE -> """
                    Expense Information
                    -------------------
                    Total Expenses: ₹%.2f

                    Existing Recommendations
                    ------------------------
                    %s
                    """.formatted(
                    context.getTotalExpenses(),
                    recommendations
            );

            case SAVINGS -> """
                    Savings Information
                    -------------------
                    Monthly Savings: ₹%.2f
                    Savings Rate: %.2f%%

                    Existing Recommendations
                    ------------------------
                    %s
                    """.formatted(
                    context.getMonthlySavings(),
                    context.getSavingsRate(),
                    recommendations
            );

            case INVESTMENT -> """
                    Investment Information
                    ----------------------
                    Risk Profile: %s

                    Existing Recommendations
                    ------------------------
                    %s
                    """.formatted(
                    context.getRiskProfile(),
                    recommendations
            );

            case INCOME -> """
                    Income Information
                    ------------------
                    Monthly Income: ₹%.2f

                    Existing Recommendations
                    ------------------------
                    %s
                    """.formatted(
                    context.getMonthlyIncome(),
                    recommendations
            );

            case BUDGET -> """
                    Budget Information
                    ------------------
                    Monthly Income: ₹%.2f
                    Total Expenses: ₹%.2f
                    Monthly Savings: ₹%.2f

                    Existing Recommendations
                    ------------------------
                    %s
                    """.formatted(
                    context.getMonthlyIncome(),
                    context.getTotalExpenses(),
                    context.getMonthlySavings(),
                    recommendations
            );

            case GENERAL -> """
                    Financial Summary
                    -----------------
                    Monthly Income: ₹%.2f
                    Total Expenses: ₹%.2f
                    Monthly Savings: ₹%.2f
                    Savings Rate: %.2f%%
                    Risk Profile: %s

                    Existing Recommendations
                    ------------------------
                    %s
                    """.formatted(
                    context.getMonthlyIncome(),
                    context.getTotalExpenses(),
                    context.getMonthlySavings(),
                    context.getSavingsRate(),
                    context.getRiskProfile(),
                    recommendations
            );
        };
    }
}