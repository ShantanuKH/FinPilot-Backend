package com.shantanu.FinPilot.budget.service;

import com.shantanu.FinPilot.budget.dto.BudgetAnalyticsResponse;
import com.shantanu.FinPilot.budget.dto.BudgetResponse;
import com.shantanu.FinPilot.budget.dto.CreateBudgetRequest;
import com.shantanu.FinPilot.budget.dto.UpdateBudgetRequest;
import com.shantanu.FinPilot.budget.entity.Budget;
import com.shantanu.FinPilot.budget.entity.BudgetStatus;
import com.shantanu.FinPilot.budget.respository.BudgetRepository;
import com.shantanu.FinPilot.common.exception.BudgetAlreadyExistsException;
import com.shantanu.FinPilot.common.exception.BudgetNotFoundException;
import com.shantanu.FinPilot.common.exception.UnauthorizedBudgetAccessException;
import com.shantanu.FinPilot.common.exception.UserNotFoundException;
import com.shantanu.FinPilot.expense.entity.Expense;
import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import com.shantanu.FinPilot.expense.repository.ExpenseRepository;
import com.shantanu.FinPilot.user.entity.User;
import com.shantanu.FinPilot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    private final ExpenseRepository expenseRepository;

    public BudgetResponse createBudget(
            String email,
            CreateBudgetRequest createBudgetRequest
    ){
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        budgetRepository.findByUserAndCategoryAndMonth(
                user,
                createBudgetRequest.getCategory(),
                createBudgetRequest.getMonth()
        ).ifPresent(existingBudget -> {
            throw new BudgetAlreadyExistsException(
                    "Budget already exists for this category and month"
            );
        });

        Budget budget = Budget.builder()
                .category(createBudgetRequest.getCategory())
                .budgetAmount(createBudgetRequest.getBudgetAmount())
                .month(createBudgetRequest.getMonth())
                .user(user)
                .build();

        Budget savedBudget = budgetRepository.save(budget);

        return BudgetResponse.builder()
                .id(savedBudget.getId())
                .category(savedBudget.getCategory())
                .budgetAmount(savedBudget.getBudgetAmount())
                .month(savedBudget.getMonth())
                .build();
    }

    public List<BudgetResponse> getMyBudgets(
            String email
    ){
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        List<Budget> budgets =
                budgetRepository.findByUser(user);

        return budgets.stream()
                .map(budget ->
                        BudgetResponse.builder()
                                .id(budget.getId())
                                .category(budget.getCategory())
                                .budgetAmount(
                                        budget.getBudgetAmount()
                                )
                                .month(budget.getMonth())
                                .build()
                )
                .toList();
    }

    public BudgetResponse updateBudget(
            Long budgetId,
            String email,
            UpdateBudgetRequest request
    ) {
        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        Budget budget = budgetRepository
                .findById(budgetId)
                .orElseThrow(
                        () -> new BudgetNotFoundException(
                                "Budget Not Found"
                        )
                );


        if (!budget.getUser().getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedBudgetAccessException(
                    "You are not authorized to update this budget"
            );

        }

        Optional<Budget> existingBudget =
                budgetRepository.findByUserAndCategoryAndMonth(
                        currentUser,
                        request.getCategory(),
                        request.getMonth()
                );

        if (existingBudget.isPresent()
                && !existingBudget.get().getId().equals(budget.getId())) {

            throw new BudgetAlreadyExistsException(
                    "Budget already exists for this category and month"
            );
        }

        budget.setCategory(
                request.getCategory()
        );

        budget.setBudgetAmount(
                request.getBudgetAmount()
        );

        budget.setMonth(
                request.getMonth()
        );
        Budget updatedBudget =
                budgetRepository.save(budget);

        return BudgetResponse.builder()
                .id(updatedBudget.getId())
                .category(updatedBudget.getCategory())
                .budgetAmount(
                        updatedBudget.getBudgetAmount()
                )
                .month(updatedBudget.getMonth())
                .build();

    }

    public void deleteBudget(
            Long budgetId,
            String email
    ){
        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        Budget budget = budgetRepository
                .findById(budgetId)
                .orElseThrow(
                        () -> new BudgetNotFoundException(
                                "Budget Not Found"
                        )
                );

        if (!budget.getUser().getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedBudgetAccessException(
                    "You are not authorized to delete this budget"
            );
        }

        budgetRepository.delete(budget);
    }

//    Methods for Budget Analytics
public List<BudgetAnalyticsResponse> getBudgetAnalytics(
        String email
) {

    User user = userRepository
            .findByEmail(email)
            .orElseThrow(
                    () -> new UserNotFoundException(
                            "User Not Found"
                    )
            );

    List<Budget> budgets =
            budgetRepository.findByUser(user);

    List<Expense> expenses =
            expenseRepository.findByUser(user);


    Map<ExpenseCategory, Double> expensesByCategory =
            expenses.stream()
                    .collect(
                            Collectors.groupingBy(
                                    Expense::getCategory,
                                    Collectors.summingDouble(
                                            Expense::getAmount
                                    )
                            )
                    );
    List<BudgetAnalyticsResponse> responses =
            new ArrayList<>();

    for (Budget budget : budgets) {

        Double spentAmount =
                expensesByCategory.getOrDefault(
                        budget.getCategory(),
                        0.0
                );

        Double remainingAmount =
                budget.getBudgetAmount()
                        - spentAmount;

        Double usagePercentage =
                budget.getBudgetAmount() > 0
                        ? (spentAmount /
                        budget.getBudgetAmount()) * 100
                        : 0.0;

        BudgetStatus status;

        if (usagePercentage > 100) {
            status = BudgetStatus.EXCEEDED;
        } else if (usagePercentage >= 80) {
            status = BudgetStatus.WARNING;
        } else {
            status = BudgetStatus.ON_TRACK;
        }

        BudgetAnalyticsResponse response =
                BudgetAnalyticsResponse.builder()
                        .budgetId(budget.getId())
                        .category(budget.getCategory())
                        .budgetAmount(
                                budget.getBudgetAmount()
                        )
                        .spentAmount(spentAmount)
                        .remainingAmount(remainingAmount)
                        .usagePercentage(usagePercentage)
                        .status(status)
                        .build();

        responses.add(response);
    }

    return responses;
}





}
