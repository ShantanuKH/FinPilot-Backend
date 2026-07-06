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


    public DashboardSummaryResponse getDashboardSummary(
            String email
    ) {

//        Find User
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        ()->new UserNotFoundException(
                                "User Not Found"
                        )
                );

//        Get User's Expenses
        List<Expense> expenses = expenseRepository
                .findByUser(user);

//        Calculate total expense
        Double totalExpenses = expenses
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        Long expenseCount = (long) expenses.size();

        Double highestExpense = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .max()
                .orElse(0.0);

        Double averageExpense = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .average()
                .orElse(0.0);

        return DashboardSummaryResponse.builder()
                .totalExpenses(totalExpenses)
                .expenseCount(expenseCount)
                .highestExpense(highestExpense)
                .averageExpense(averageExpense)
                .build();
    }

    public List<CategoryBreakdownResponse> getCategoryBreakdown(
            String email
    ) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        Map<ExpenseCategory, Double> categoryTotals =
                expenses.stream()
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
                .map(entry ->
                        CategoryBreakdownResponse.builder()
                                .category(entry.getKey())
                                .totalAmount(entry.getValue())
                                .build()
                )
                .toList();
    }

//    Monthly Expenses
        public List<MonthlySummaryResponse>
        getMonthlySummary(
                String email
        ) {
            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow(
                            () -> new UserNotFoundException(
                                    "User Not Found"
                            )
                    );
            List<Expense> expenses =
                    expenseRepository.findByUser(user);

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
                    .map(entry ->
                            MonthlySummaryResponse.builder()
                                    .month(
                                            entry.getKey().toString()
                                    )
                                    .totalAmount(
                                            entry.getValue()
                                    )
                                    .build()
                    )
                    .toList();


        }

    public BudgetHealthResponse getBudgetHealth(
            String email
    ) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );
        List<Expense> expenses =
                expenseRepository.findByUser(user);

        Double totalExpenses = expenses.stream()
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
