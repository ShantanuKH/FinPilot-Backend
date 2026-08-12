package com.shantanu.FinPilot.dashboard.service;

import com.shantanu.FinPilot.common.exception.UserNotFoundException;
import com.shantanu.FinPilot.dashboard.dto.BudgetHealthResponse;
import com.shantanu.FinPilot.dashboard.dto.CategoryBreakdownResponse;
import com.shantanu.FinPilot.dashboard.dto.DashboardSummaryResponse;
import com.shantanu.FinPilot.dashboard.dto.MonthlySummaryResponse;
import com.shantanu.FinPilot.expense.entity.Expense;
import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import com.shantanu.FinPilot.expense.repository.ExpenseRepository;
import com.shantanu.FinPilot.user.entity.User;
import com.shantanu.FinPilot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    // =========================================================
    // Helper Methods
    // =========================================================

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User Not Found"));
    }

    private List<Expense> getUserExpenses(User user) {
        return expenseRepository.findByUser(user);
    }

    private List<Expense> getCurrentMonthExpenses(
            List<Expense> expenses
    ) {
        YearMonth currentMonth = YearMonth.now();

        return expenses.stream()
                .filter(expense ->
                        YearMonth.from(expense.getExpenseDate())
                                .equals(currentMonth)
                )
                .toList();
    }

    // =========================================================
    // Dashboard Summary
    // =========================================================

    public DashboardSummaryResponse getDashboardSummary(
            String email
    ) {

        User user = getUserByEmail(email);

        List<Expense> expenses =
                getUserExpenses(user);

        List<Expense> currentMonthExpenses =
                getCurrentMonthExpenses(expenses);

        Double totalExpenses =
                currentMonthExpenses.stream()
                        .mapToDouble(Expense::getAmount)
                        .sum();

        Long expenseCount =
                (long) currentMonthExpenses.size();

        Double highestExpense =
                currentMonthExpenses.stream()
                        .mapToDouble(Expense::getAmount)
                        .max()
                        .orElse(0.0);

        Double averageExpense =
                currentMonthExpenses.stream()
                        .mapToDouble(Expense::getAmount)
                        .average()
                        .stream()
                        .map(avg ->
                                Math.round(avg * 100.0) / 100.0
                        )
                        .findFirst()
                        .orElse(0.0);

        return DashboardSummaryResponse.builder()
                .totalExpenses(totalExpenses)
                .expenseCount(expenseCount)
                .highestExpense(highestExpense)
                .averageExpense(averageExpense)
                .build();
    }

    // =========================================================
    // Category Breakdown
    // =========================================================

    public List<CategoryBreakdownResponse> getCategoryBreakdown(
            String email
    ) {

        User user = getUserByEmail(email);

        List<Expense> expenses =
                getUserExpenses(user);

        // Only use current month's expenses
        List<Expense> currentMonthExpenses =
                getCurrentMonthExpenses(expenses);

        Map<ExpenseCategory, Double> categoryTotals =
                currentMonthExpenses.stream()
                        .collect(
                                Collectors.groupingBy(
                                        Expense::getCategory,
                                        Collectors.summingDouble(
                                                Expense::getAmount
                                        )
                                )
                        );

        return categoryTotals.entrySet()
                .stream()
                .sorted(
                        Map.Entry
                                .<ExpenseCategory, Double>
                                        comparingByValue()
                                .reversed()
                )
                .map(entry ->
                        CategoryBreakdownResponse.builder()
                                .category(entry.getKey())
                                .totalAmount(entry.getValue())
                                .build()
                )
                .toList();
    }

    // =========================================================
    // Monthly Summary
    // =========================================================

    public List<MonthlySummaryResponse> getMonthlySummary(
            String email
    ) {

        User user = getUserByEmail(email);

        List<Expense> expenses =
                getUserExpenses(user);

        /*
         * Monthly Summary intentionally uses ALL expenses
         * because this chart shows spending across months.
         */

        Map<YearMonth, Double> monthlyTotals =
                expenses.stream()
                        .collect(
                                Collectors.groupingBy(
                                        expense ->
                                                YearMonth.from(
                                                        expense.getExpenseDate()
                                                ),
                                        Collectors.summingDouble(
                                                Expense::getAmount
                                        )
                                )
                        );

        return monthlyTotals.entrySet()
                .stream()
                .sorted(
                        Map.Entry.comparingByKey()
                )
                .map(entry ->
                        MonthlySummaryResponse.builder()
                                .month(entry.getKey().toString())
                                .totalAmount(entry.getValue())
                                .build()
                )
                .toList();
    }

    // =========================================================
    // Budget Health
    // =========================================================

    public BudgetHealthResponse getBudgetHealth(
            String email
    ) {

        User user = getUserByEmail(email);

        List<Expense> expenses =
                getUserExpenses(user);

        List<Expense> currentMonthExpenses =
                getCurrentMonthExpenses(expenses);

        Double totalExpenses =
                currentMonthExpenses.stream()
                        .mapToDouble(Expense::getAmount)
                        .sum();

        Double monthlyIncome =
                user.getMonthlyIncome();

        Double remainingAmount =
                monthlyIncome - totalExpenses;

        Double savingRate =
                monthlyIncome > 0
                        ? (remainingAmount / monthlyIncome) * 100
                        : 0.0;

        return BudgetHealthResponse.builder()
                .monthlyIncome(monthlyIncome)
                .totalExpenses(totalExpenses)
                .remainingAmount(remainingAmount)
                .savingRate(savingRate)
                .build();
    }
}