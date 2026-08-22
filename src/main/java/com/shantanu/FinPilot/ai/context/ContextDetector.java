package com.shantanu.FinPilot.ai.context;

import org.springframework.stereotype.Component;

@Component
public class ContextDetector {

    public ContextType detect(String message) {

        String lowerCaseMessage = message.toLowerCase();

        if (containsAny(lowerCaseMessage,
                "expense", "spend", "spending", "cost", "payment")) {
            return ContextType.EXPENSE;
        }

        if (containsAny(lowerCaseMessage,
                "budget", "limit")) {
            return ContextType.BUDGET;
        }

        if (containsAny(lowerCaseMessage,
                "invest", "investment", "sip", "mutual fund", "stock", "equity")) {
            return ContextType.INVESTMENT;
        }

        if (containsAny(lowerCaseMessage,
                "save", "saving", "savings", "emergency fund")) {
            return ContextType.SAVINGS;
        }

        if (containsAny(lowerCaseMessage,
                "salary", "income", "earn", "earning")) {
            return ContextType.INCOME;
        }

        return ContextType.GENERAL;
    }

    private boolean containsAny(String message, String... keywords) {

        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}