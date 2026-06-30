package com.shantanu.FinPilot.expense.service;

import com.shantanu.FinPilot.common.exception.ExpenseNotFoundException;
import com.shantanu.FinPilot.common.exception.UnauthorizedExpenseAccessException;
import com.shantanu.FinPilot.common.exception.UserNotFoundException;
import com.shantanu.FinPilot.expense.dto.CreateExpenseRequest;
import com.shantanu.FinPilot.expense.dto.ExpenseResponse;
import com.shantanu.FinPilot.expense.dto.UpdateExpenseRequest;
import com.shantanu.FinPilot.expense.entity.Expense;
import com.shantanu.FinPilot.expense.repository.ExpenseRepository;
import com.shantanu.FinPilot.user.entity.User;
import com.shantanu.FinPilot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseResponse createExpense(
            String email,
            CreateExpenseRequest createExpenseRequest
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        Expense expense = Expense.builder()
                .title(createExpenseRequest.getTitle())
                .amount(createExpenseRequest.getAmount())
                .description(createExpenseRequest.getDescription())
                .expenseDate(createExpenseRequest.getExpenseDate())
                .category(createExpenseRequest.getCategory())
                .user(user)
                .build();

        Expense savedExpense = expenseRepository.save(expense);

        return ExpenseResponse.builder()
                .id(savedExpense.getId())
                .title(savedExpense.getTitle())
                .amount(savedExpense.getAmount())
                .description(savedExpense.getDescription())
                .expenseDate(savedExpense.getExpenseDate())
                .category(savedExpense.getCategory())
                .build();
    }

    public List<ExpenseResponse> getMyExpenses(
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        return expenses.stream()
                .map(expense ->
                        ExpenseResponse.builder()
                                .id(expense.getId())
                                .title(expense.getTitle())
                                .amount(expense.getAmount())
                                .description(expense.getDescription())
                                .expenseDate(expense.getExpenseDate())
                                .category(expense.getCategory())
                                .build()
                )
                .toList();
    }

    public void deleteExpense(
            Long expenseId,
            String email
    ) {

        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        Expense expense = expenseRepository
                .findById(expenseId)
                .orElseThrow(
                        () -> new ExpenseNotFoundException(
                                "Expense Not Found"
                        )
                );

        if (!expense.getUser().getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedExpenseAccessException(
                    "You are not authorized to delete this expense"
            );
        }

        expenseRepository.delete(expense);
    }

    public ExpenseResponse updateExpense(
            Long expenseId,
            String email,
            UpdateExpenseRequest updateExpenseRequest
    ) {

        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        Expense expense = expenseRepository
                .findById(expenseId)
                .orElseThrow(
                        () -> new ExpenseNotFoundException(
                                "Expense Not Found"
                        )
                );

        if (!expense.getUser().getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedExpenseAccessException(
                    "You are not authorized to update this expense"
            );
        }

        expense.setTitle(updateExpenseRequest.getTitle());

        expense.setAmount(updateExpenseRequest.getAmount());

        expense.setDescription(
                updateExpenseRequest.getDescription()
        );

        expense.setExpenseDate(
                updateExpenseRequest.getExpenseDate()
        );

        expense.setCategory(
                updateExpenseRequest.getCategory()
        );

        Expense updatedExpense =
                expenseRepository.save(expense);

        return ExpenseResponse.builder()
                .id(updatedExpense.getId())
                .title(updatedExpense.getTitle())
                .amount(updatedExpense.getAmount())
                .description(updatedExpense.getDescription())
                .expenseDate(updatedExpense.getExpenseDate())
                .category(updatedExpense.getCategory())
                .build();
    }
}