package com.shantanu.FinPilot.expense.service;

import com.shantanu.FinPilot.expense.dto.CreateExpenseRequest;
import com.shantanu.FinPilot.expense.dto.ExpenseResponse;
import com.shantanu.FinPilot.expense.entity.Expense;
import com.shantanu.FinPilot.expense.repository.ExpenseRepository;
import com.shantanu.FinPilot.user.entity.User;
import com.shantanu.FinPilot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseResponse createExpense(
            String email,
            CreateExpenseRequest createExpenseRequest
    ){
        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        ()-> new RuntimeException(
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


}
