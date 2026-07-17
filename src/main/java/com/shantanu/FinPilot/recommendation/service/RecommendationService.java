package com.shantanu.FinPilot.recommendation.service;

import com.shantanu.FinPilot.budget.entity.Budget;
import com.shantanu.FinPilot.budget.respository.BudgetRepository;
import com.shantanu.FinPilot.common.exception.UserNotFoundException;
import com.shantanu.FinPilot.expense.entity.Expense;
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

    public RecommendationResponse getRecommendations(String email) {

        List<String> recommendations = generateRecommendations(email);

        return RecommendationResponse.builder()
                .recommendations(recommendations)
                .build();
    }

    public List<String> generateRecommendations(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User Not Found"));

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        List<Budget> budgets =
                budgetRepository.findByUser(user);

        List<Investment> investments =
                investmentRepository.findByUser(user);

        List<String> recommendations = new ArrayList<>();

        addSavingsRecommendation(recommendations, user, expenses);

        addInvestmentRiskRecommendation(recommendations, user, investments);

        addBudgetRecommendation(recommendations, budgets, expenses);

        addDiversificationRecommendation(recommendations, investments);

        addEmergencyFundRecommendation(recommendations, user, expenses);

        return recommendations;
    }


    public void addSavingsRecommendation(
            List<String> recommendations,
            User user,
            List<Expense> expenses
    ){
        Double monthlyIncome = user.getMonthlyIncome();

        Double totalExpense = expenses.stream()
                .mapToDouble(
                        Expense::getAmount
                )
                .sum();


        Double monthlySavings =
                monthlyIncome - totalExpense;

        Double savingsRate =
                monthlyIncome > 0
                        ? (monthlySavings / monthlyIncome) * 100
                        : 0.0;

        if (savingsRate < 10) {

            recommendations.add(
                    "Your savings rate is very low. Reduce unnecessary expenses and aim to save at least 20% of your monthly income."
            );

        } else if (savingsRate < 20) {

            recommendations.add(
                    "Your savings rate is below the recommended level. Try increasing your monthly savings."
            );

        } else {

            recommendations.add(
                    "Great job! Your savings rate is healthy. Keep investing consistently."
            );
        }

    }

    private void addInvestmentRiskRecommendation(
            List<String> recommendations,
            User user,
            List<Investment> investments
    ) {

        Double highRiskAmount = 0.0;

        for (Investment investment : investments) {

            if (investment.getInvestmentType() == InvestmentType.STOCK
                    ||
                    investment.getInvestmentType() == InvestmentType.CRYPTO) {

                highRiskAmount =
                        highRiskAmount +
                                investment.getAmount();
            }
        }

        Double totalInvestment = investments.stream()
                .mapToDouble(Investment::getAmount)
                .sum();

        Double highRiskPercentage =
                totalInvestment > 0 ?
                        (highRiskAmount / totalInvestment) * 100
                        : 0.0;

        RiskProfile portfolioRisk;

        if (highRiskPercentage > 60) {
            portfolioRisk = RiskProfile.HIGH;
        } else if (highRiskPercentage >= 30) {
            portfolioRisk = RiskProfile.MODERATE;
        } else {
            portfolioRisk = RiskProfile.LOW;
        }


        RiskProfile userRiskProfile = user.getRiskProfile();

        if (userRiskProfile == portfolioRisk) {

            recommendations.add(
                    "Your investment portfolio matches your selected risk profile. Keep reviewing it periodically."
            );

        } else if (portfolioRisk == RiskProfile.HIGH) {

            recommendations.add(
                    "Your portfolio is riskier than your selected risk profile. Consider reducing exposure to high-risk investments like Stocks and Crypto."
            );

        } else {

            recommendations.add(
                    "Your portfolio is more conservative than your selected risk profile. If it aligns with your financial goals, you may consider increasing exposure to growth-oriented investments."
            );
        }

    }

    private void addBudgetRecommendation(
            List<String> recommendations,
            List<Budget> budgets,
            List<Expense> expenses
    ){


        for (Budget budget : budgets) {

            Double spentAmount = expenses.stream()
                    .filter(expense ->
                            expense.getCategory()
                                    .equals(budget.getCategory())
                    )
                    .mapToDouble(Expense::getAmount)
                    .sum();

            Double usagePercentage =
                    budget.getBudgetAmount() > 0
                            ? (spentAmount / budget.getBudgetAmount()) * 100
                            : 0.0;



            if (usagePercentage > 100) {

                recommendations.add(
                        "You have exceeded your "
                                + budget.getCategory()
                                + " budget. Consider reducing expenses in this category."
                );

            } else if (usagePercentage >= 80) {

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

    private void addDiversificationRecommendation(
            List<String> recommendations,
            List<Investment> investments
    ){

        Double totalInvestment =
                investments.stream()
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .sum();

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

        for (Map.Entry<InvestmentType, Double> entry
                : investmentAllocation.entrySet()) {

            Double investmentPercentage =
                    totalInvestment > 0
                            ? (entry.getValue() / totalInvestment) * 100
                            : 0.0;

            if (investmentPercentage > 70) {

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


    private void addEmergencyFundRecommendation(
            List<String> recommendations,
            User user,
            List<Expense> expenses
    ) {

        Double totalExpense = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        Double monthlyIncome = user.getMonthlyIncome();

        Double monthlySavings =
                monthlyIncome - totalExpense;

        if (monthlySavings <= 0) {

            recommendations.add(
                    "Your current expenses leave little or no room for savings. Try reducing non-essential expenses before building an emergency fund."
            );

        } else if (monthlySavings < (monthlyIncome * 0.20)) {

            recommendations.add(
                    "Consider increasing your monthly savings and build an emergency fund covering at least 3 to 6 months of your expenses."
            );

        } else {

            recommendations.add(
                    "Great job! Continue setting aside part of your monthly savings until you have an emergency fund covering 3 to 6 months of expenses."
            );
        }
    }
}