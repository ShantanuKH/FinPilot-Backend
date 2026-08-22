package com.shantanu.FinPilot.recommendation.service;

import com.shantanu.FinPilot.ai.dto.AiFinancialContext;
import com.shantanu.FinPilot.budget.entity.Budget;
import com.shantanu.FinPilot.budget.respository.BudgetRepository;
import com.shantanu.FinPilot.common.exception.UserNotFoundException;
import com.shantanu.FinPilot.expense.entity.Expense;
import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import com.shantanu.FinPilot.expense.repository.ExpenseRepository;
import com.shantanu.FinPilot.investment.entity.Investment;
import com.shantanu.FinPilot.investment.entity.InvestmentType;
import com.shantanu.FinPilot.investment.repository.InvestmentRepository;
import com.shantanu.FinPilot.recommendation.dto.RecommendationResponse;
import com.shantanu.FinPilot.user.entity.RiskProfile;
import com.shantanu.FinPilot.user.entity.User;
import com.shantanu.FinPilot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final InvestmentRepository investmentRepository;

    // =========================================================
    // Recommendation Thresholds
    // =========================================================

    // Savings Rate (%)
    private static final double LOW_SAVINGS_RATE = 10.0;
    private static final double RECOMMENDED_SAVINGS_RATE = 20.0;

    // Portfolio Risk (%)
    private static final double MODERATE_RISK_THRESHOLD = 30.0;
    private static final double HIGH_RISK_THRESHOLD = 60.0;

    // Diversification (%)
    private static final double DIVERSIFICATION_THRESHOLD = 70.0;

    // Budget Usage (%)
    private static final double BUDGET_WARNING_THRESHOLD = 80.0;
    private static final double BUDGET_EXCEEDED_THRESHOLD = 100.0;

    // Emergency Fund
    private static final double EMERGENCY_FUND_SAVINGS_THRESHOLD = 0.20;

    // =========================================================
    // User / Data Helpers
    // =========================================================

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User Not Found"
                        ));
    }

    private List<Expense> getUserExpenses(User user) {

        return expenseRepository.findByUser(user);
    }

    private List<Budget> getUserBudgets(User user) {

        return budgetRepository.findByUser(user);
    }

    private List<Investment> getUserInvestments(User user) {

        return investmentRepository.findByUser(user);
    }

    /**
     * Checks whether the user has entered any financial data.
     *
     * A new user should not receive artificial financial
     * recommendations when there is no financial activity.
     */
    private boolean hasFinancialData(
            List<Expense> expenses,
            List<Budget> budgets,
            List<Investment> investments
    ) {

        return !expenses.isEmpty()
                || !budgets.isEmpty()
                || !investments.isEmpty();
    }

    // =========================================================
    // Financial Calculations
    // =========================================================

    private double getMonthlyIncome(User user) {

        return user.getMonthlyIncome() != null
                ? user.getMonthlyIncome()
                : 0.0;
    }

    private Double calculateTotalExpenses(
            List<Expense> expenses
    ) {

        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    private Double calculateMonthlySavings(
            User user,
            List<Expense> expenses
    ) {

        return getMonthlyIncome(user)
                - calculateTotalExpenses(expenses);
    }

    private Double calculateSavingsRate(
            User user,
            List<Expense> expenses
    ) {

        double monthlyIncome =
                getMonthlyIncome(user);

        if (monthlyIncome <= 0) {
            return 0.0;
        }

        return (
                calculateMonthlySavings(
                        user,
                        expenses
                ) / monthlyIncome
        ) * 100;
    }

    // =========================================================
    // Expense Analysis
    // =========================================================

    /**
     * Groups all expenses by category.
     *
     * @param expenses User's expenses.
     * @return Map containing the total expense for each category.
     */
    private Map<ExpenseCategory, Double> getExpensesByCategory(
            List<Expense> expenses
    ) {

        return expenses.stream()
                .collect(
                        Collectors.groupingBy(
                                Expense::getCategory,
                                Collectors.summingDouble(
                                        Expense::getAmount
                                )
                        )
                );
    }

    // =========================================================
    // Recommendations
    // =========================================================

    /**
     * Generates personalized financial recommendations
     * for the authenticated user.
     *
     * For a brand-new user with no financial data, a
     * friendly onboarding recommendation is returned.
     */
    public RecommendationResponse getRecommendations(
            String email
    ) {

        List<String> recommendations =
                generateRecommendations(email);

        return RecommendationResponse.builder()
                .recommendations(recommendations)
                .build();
    }

    /**
     * Generates all financial recommendations for the user
     * by analysing expenses, budgets and investments.
     *
     * New users receive an onboarding message instead of
     * artificial financial recommendations.
     */
    public List<String> generateRecommendations(
            String email
    ) {

        User user =
                getUserByEmail(email);

        List<Expense> expenses =
                getUserExpenses(user);

        List<Budget> budgets =
                getUserBudgets(user);

        List<Investment> investments =
                getUserInvestments(user);

        // =====================================================
        // New User Handling
        // =====================================================

        if (!hasFinancialData(
                expenses,
                budgets,
                investments
        )) {

            return List.of(
                    "Welcome to FinPilot! Start adding your expenses, budgets or investments to unlock personalized financial recommendations."
            );
        }

        // =====================================================
        // Existing User
        // =====================================================

        Map<ExpenseCategory, Double> expensesByCategory =
                getExpensesByCategory(expenses);

        List<String> recommendations =
                new ArrayList<>();

        // Savings analysis
        addSavingsRecommendation(
                recommendations,
                user,
                expenses
        );

        // Investment risk analysis
        addInvestmentRiskRecommendation(
                recommendations,
                user,
                investments
        );

        // Budget analysis
        addBudgetRecommendation(
                recommendations,
                budgets,
                expensesByCategory
        );

        // Portfolio diversification analysis
        addDiversificationRecommendation(
                recommendations,
                investments
        );

        // Emergency fund analysis
        addEmergencyFundRecommendation(
                recommendations,
                user,
                expenses
        );

        return recommendations;
    }

    // =========================================================
    // AI Financial Context
    // =========================================================

    /**
     * Builds the financial context required by the AI module
     * to generate personalized financial insights.
     *
     * For a new user, the financial metrics will safely contain
     * zero values and the recommendations list will remain empty.
     */
    public AiFinancialContext buildFinancialContext(
            String email
    ) {

        User user =
                getUserByEmail(email);

        List<Expense> expenses =
                getUserExpenses(user);

        List<Budget> budgets =
                getUserBudgets(user);

        List<Investment> investments =
                getUserInvestments(user);

        // =====================================================
        // Financial Metrics
        // =====================================================

        Double monthlyIncome =
                getMonthlyIncome(user);

        Double totalExpenses =
                calculateTotalExpenses(expenses);

        Double monthlySavings =
                calculateMonthlySavings(
                        user,
                        expenses
                );

        Double savingsRate =
                calculateSavingsRate(
                        user,
                        expenses
                );

        Map<ExpenseCategory, Double> expensesByCategory =
                getExpensesByCategory(expenses);

        // =====================================================
        // Recommendations
        // =====================================================

        List<String> recommendations =
                new ArrayList<>();

        /*
         * Only generate financial recommendations when
         * the user actually has financial data.
         */
        if (hasFinancialData(
                expenses,
                budgets,
                investments
        )) {

            addSavingsRecommendation(
                    recommendations,
                    user,
                    expenses
            );

            addInvestmentRiskRecommendation(
                    recommendations,
                    user,
                    investments
            );

            addBudgetRecommendation(
                    recommendations,
                    budgets,
                    expensesByCategory
            );

            addDiversificationRecommendation(
                    recommendations,
                    investments
            );

            addEmergencyFundRecommendation(
                    recommendations,
                    user,
                    expenses
            );
        }

        return AiFinancialContext.builder()
                .monthlyIncome(monthlyIncome)
                .totalExpenses(totalExpenses)
                .monthlySavings(monthlySavings)
                .savingsRate(savingsRate)
                .riskProfile(user.getRiskProfile())
                .recommendations(recommendations)
                .build();
    }

    // =========================================================
    // Savings Recommendation
    // =========================================================

    /**
     * Generates savings recommendations based on
     * the user's savings rate.
     */
    private void addSavingsRecommendation(
            List<String> recommendations,
            User user,
            List<Expense> expenses
    ) {

        Double savingsRate =
                calculateSavingsRate(
                        user,
                        expenses
                );

        if (savingsRate < LOW_SAVINGS_RATE) {

            recommendations.add(
                    "Your savings rate is very low. Reduce unnecessary expenses and aim to save at least 20% of your monthly income."
            );

        } else if (
                savingsRate < RECOMMENDED_SAVINGS_RATE
        ) {

            recommendations.add(
                    "Your savings rate is below the recommended level. Try increasing your monthly savings."
            );

        } else {

            recommendations.add(
                    "Great job! Your savings rate is healthy. Keep investing consistently."
            );
        }
    }

    // =========================================================
    // Investment Risk Recommendation
    // =========================================================

    /**
     * Compares the user's portfolio risk with their selected
     * risk profile and generates appropriate recommendations.
     */
    private void addInvestmentRiskRecommendation(
            List<String> recommendations,
            User user,
            List<Investment> investments
    ) {

        /*
         * No investments yet.
         *
         * Do not create a fake portfolio-risk recommendation.
         */
        if (investments.isEmpty()) {

            recommendations.add(
                    "You haven't added any investments yet. Once you start investing, FinPilot can help you review your portfolio risk."
            );

            return;
        }

        Double highRiskAmount = 0.0;

        for (Investment investment : investments) {

            if (
                    investment.getInvestmentType()
                            == InvestmentType.STOCK
                            ||
                            investment.getInvestmentType()
                                    == InvestmentType.CRYPTO
            ) {

                highRiskAmount +=
                        investment.getAmount();
            }
        }

        Double totalInvestment =
                investments.stream()
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .sum();

        Double highRiskPercentage =
                totalInvestment > 0
                        ? (
                        highRiskAmount
                                / totalInvestment
                ) * 100
                        : 0.0;

        RiskProfile portfolioRisk;

        if (
                highRiskPercentage
                        > HIGH_RISK_THRESHOLD
        ) {

            portfolioRisk =
                    RiskProfile.HIGH;

        } else if (
                highRiskPercentage
                        >= MODERATE_RISK_THRESHOLD
        ) {

            portfolioRisk =
                    RiskProfile.MODERATE;

        } else {

            portfolioRisk =
                    RiskProfile.LOW;
        }

        RiskProfile userRiskProfile =
                user.getRiskProfile();

        /*
         * If the user hasn't selected a risk profile,
         * avoid comparing against null.
         */
        if (userRiskProfile == null) {

            recommendations.add(
                    "Your portfolio has been added successfully. Set your risk profile in Settings to receive more personalized investment recommendations."
            );

            return;
        }

        if (
                userRiskProfile == portfolioRisk
        ) {

            recommendations.add(
                    "Your investment portfolio matches your selected risk profile. Keep reviewing it periodically."
            );

        } else if (
                portfolioRisk == RiskProfile.HIGH
        ) {

            recommendations.add(
                    "Your portfolio is riskier than your selected risk profile. Consider reducing exposure to high-risk investments like Stocks and Crypto."
            );

        } else {

            recommendations.add(
                    "Your portfolio is more conservative than your selected risk profile. If it aligns with your financial goals, you may consider increasing exposure to growth-oriented investments."
            );
        }
    }

    // =========================================================
    // Budget Recommendation
    // =========================================================

    /**
     * Compares actual spending against each budget
     * and generates budget-related recommendations.
     */
    private void addBudgetRecommendation(
            List<String> recommendations,
            List<Budget> budgets,
            Map<ExpenseCategory, Double> expensesByCategory
    ) {

        /*
         * No budgets yet.
         *
         * This is not an error.
         */
        if (budgets.isEmpty()) {

            recommendations.add(
                    "You haven't created any budgets yet. Setting category-based budgets can help you control your monthly spending."
            );

            return;
        }

        for (Budget budget : budgets) {

            Double spentAmount =
                    expensesByCategory.getOrDefault(
                            budget.getCategory(),
                            0.0
                    );

            Double usagePercentage =
                    budget.getBudgetAmount() > 0
                            ? (
                            spentAmount
                                    / budget.getBudgetAmount()
                    ) * 100
                            : 0.0;

            if (
                    usagePercentage
                            > BUDGET_EXCEEDED_THRESHOLD
            ) {

                recommendations.add(
                        "You have exceeded your "
                                + budget.getCategory()
                                + " budget. Consider reducing expenses in this category."
                );

            } else if (
                    usagePercentage
                            >= BUDGET_WARNING_THRESHOLD
            ) {

                recommendations.add(
                        "You are close to exceeding your "
                                + budget.getCategory()
                                + " budget."
                );

            } else {

                recommendations.add(
                        "Great job! You are managing your "
                                + budget.getCategory()
                                + " budget well."
                );
            }
        }
    }

    // =========================================================
    // Portfolio Diversification
    // =========================================================

    /**
     * Analyses the user's investment portfolio and checks
     * whether it is sufficiently diversified.
     */
    private void addDiversificationRecommendation(
            List<String> recommendations,
            List<Investment> investments
    ) {

        /*
         * Do not say a portfolio is diversified when there
         * are no investments.
         */
        if (investments.isEmpty()) {

            recommendations.add(
                    "Once you add investments to FinPilot, we can analyze your portfolio diversification."
            );

            return;
        }

        Double totalInvestment =
                investments.stream()
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .sum();

        if (totalInvestment <= 0) {

            recommendations.add(
                    "Add investments with valid amounts to receive portfolio diversification insights."
            );

            return;
        }

        Map<InvestmentType, Double> investmentAllocation =
                investments.stream()
                        .collect(
                                Collectors.groupingBy(
                                        Investment::getInvestmentType,
                                        Collectors.summingDouble(
                                                Investment::getAmount
                                        )
                                )
                        );

        boolean isDiversified = true;

        for (
                Map.Entry<InvestmentType, Double> entry
                : investmentAllocation.entrySet()
        ) {

            Double investmentPercentage =
                    (
                            entry.getValue()
                                    / totalInvestment
                    ) * 100;

            if (
                    investmentPercentage
                            > DIVERSIFICATION_THRESHOLD
            ) {

                recommendations.add(
                        "Your portfolio is highly concentrated in "
                                + entry.getKey()
                                + ". Consider diversifying into other investment types."
                );

                isDiversified = false;
            }
        }

        if (isDiversified) {

            recommendations.add(
                    "Great job! Your investment portfolio is well diversified across multiple asset classes."
            );
        }
    }

    // =========================================================
    // Emergency Fund
    // =========================================================

    /**
     * Generates recommendations for building and maintaining
     * an emergency fund based on the user's monthly savings.
     */
    private void addEmergencyFundRecommendation(
            List<String> recommendations,
            User user,
            List<Expense> expenses
    ) {

        Double monthlyIncome =
                getMonthlyIncome(user);

        Double monthlySavings =
                calculateMonthlySavings(
                        user,
                        expenses
                );

        /*
         * Without income information, we cannot provide a
         * meaningful emergency-fund calculation.
         */
        if (monthlyIncome <= 0) {

            recommendations.add(
                    "Add your monthly income in your financial profile to receive personalized emergency fund recommendations."
            );

            return;
        }

        if (monthlySavings <= 0) {

            recommendations.add(
                    "Your current expenses leave little or no room for savings. Try reducing non-essential expenses before building an emergency fund."
            );

        } else if (
                monthlySavings
                        < (
                        monthlyIncome
                                * EMERGENCY_FUND_SAVINGS_THRESHOLD
                )
        ) {

            recommendations.add(
                    "Consider increasing your monthly savings and build an emergency fund covering at least 3 to 6 months of expenses."
            );

        } else {

            recommendations.add(
                    "Great job! Continue setting aside part of your monthly savings until you have an emergency fund covering 3 to 6 months of expenses."
            );
        }
    }
}