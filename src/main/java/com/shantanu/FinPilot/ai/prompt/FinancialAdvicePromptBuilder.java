package com.shantanu.FinPilot.ai.prompt;

import com.shantanu.FinPilot.ai.dto.AiFinancialContext;
import org.springframework.stereotype.Component;

@Component
public class FinancialAdvicePromptBuilder {

    public String buildFinancialAdvicePrompt(
            AiFinancialContext context
    ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are FinPilot AI, an experienced and practical personal financial advisor.

                Your responsibility is to analyse the user's financial profile and generate
                personalised, practical and easy-to-understand financial advice.

                Follow these rules carefully:

                - Use ONLY the financial information provided below.
                - Never assume or invent financial data.
                - Do not exaggerate or make unrealistic claims.
                - Do not guarantee investment returns or financial outcomes.
                - Prioritise recommendations that will have the biggest positive impact.
                - Explain the reasoning behind important recommendations.
                - Keep your advice concise, practical and actionable.
                - Maintain a positive, professional and encouraging tone.

                """);

        prompt.append("""
                User Financial Profile
                ----------------------
                Monthly Income: ₹%.2f
                Total Expenses: ₹%.2f
                Monthly Savings: ₹%.2f
                Savings Rate: %.2f%%
                Risk Profile: %s

                """.formatted(
                context.getMonthlyIncome(),
                context.getTotalExpenses(),
                context.getMonthlySavings(),
                context.getSavingsRate(),
                context.getRiskProfile()
        ));

        prompt.append("""
                Existing Financial Recommendations
                ----------------------------------
                """);

        for (String recommendation : context.getRecommendations()) {
            prompt.append("- ")
                    .append(recommendation)
                    .append("\n");
        }

        prompt.append("""

                Generate a financial report using the above information.

                Response Guidelines

                - Summary should be between 80 and 150 words.
                - Strengths should contain 3 to 5 concise points.
                - Improvements should contain 3 to 5 practical suggestions.
                - Action items should contain 3 to 5 prioritised next steps.
                - Motivation should be one short encouraging paragraph.

                Return ONLY a valid JSON object.

                Use exactly this structure:

                {
                  "summary": "...",
                  "strengths": [
                    "...",
                    "..."
                  ],
                  "improvements": [
                    "...",
                    "..."
                  ],
                  "actionItems": [
                    "...",
                    "..."
                  ],
                  "motivation": "..."
                }

                Do not include markdown.
                Do not include code fences.
                Do not include explanations.
                Do not include any additional fields.
                Return only the JSON object.
                """);

        return prompt.toString();
    }
}