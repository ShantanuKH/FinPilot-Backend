package com.shantanu.FinPilot.profile.service;

import com.shantanu.FinPilot.common.exception.UserNotFoundException;
import com.shantanu.FinPilot.expense.entity.Expense;
import com.shantanu.FinPilot.expense.repository.ExpenseRepository;
import com.shantanu.FinPilot.profile.dto.FinancialHealthResponse;
import com.shantanu.FinPilot.profile.dto.ProfileResponse;
import com.shantanu.FinPilot.profile.dto.UpdateProfileRequest;
import com.shantanu.FinPilot.profile.entity.FinancialHealthStatus;
import com.shantanu.FinPilot.user.entity.User;
import com.shantanu.FinPilot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public ProfileResponse getProfile(
            String email
    ) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        return ProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .monthlyIncome(user.getMonthlyIncome())
                .riskProfile(user.getRiskProfile())
                .currency(user.getCurrency())
                .build();
    }

    public ProfileResponse updateProfile(
            String email,
            UpdateProfileRequest request
    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        user.setMonthlyIncome(
                request.getMonthlyIncome()
        );

        user.setRiskProfile(
                request.getRiskProfile()
        );

        user.setCurrency(
                request.getCurrency()
        );

        User updatedUser =
                userRepository.save(user);

        return ProfileResponse.builder()
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .email(updatedUser.getEmail())
                .monthlyIncome(updatedUser.getMonthlyIncome())
                .riskProfile(updatedUser.getRiskProfile())
                .currency(updatedUser.getCurrency())
                .build();
    }


    public FinancialHealthResponse getFinancialHealth(
            String email
    ){

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        ()->new UserNotFoundException("User doew not exists")
                );

        List<Expense> expense = expenseRepository.findByUser(user);

//        Get Montly Income
       Double monthlyIncome = user.getMonthlyIncome();


//      Get total amount spent
        Double totalExpenses = expense.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

//      Monthly Savings
        Double monthlySavings = monthlyIncome - totalExpenses;

//        Saving Rate
        Double savingsRate =
                monthlyIncome != null &&
                        monthlyIncome > 0
                        ? (monthlySavings /
                        monthlyIncome) * 100
                        : 0.0;

        savingsRate = Math.round(
                        savingsRate * 100.0
                ) / 100.0;

        FinancialHealthStatus status;

        if (savingsRate >= 50) {

            status = FinancialHealthStatus.EXCELLENT;

        } else if (savingsRate >= 20) {

            status = FinancialHealthStatus.GOOD;

        } else {

            status = FinancialHealthStatus.NEEDS_IMPROVEMENT;
        }

        return FinancialHealthResponse.builder()
                .monthlyIncome(monthlyIncome)
                .totalExpenses(totalExpenses)
                .monthlySavings(monthlySavings)
                .savingsRate(savingsRate)
                .status(status)
                .build();

    }

}