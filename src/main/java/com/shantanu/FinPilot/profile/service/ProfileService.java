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

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    // Financial Health Thresholds
    private static final double EXCELLENT_SAVINGS_RATE = 50.0;
    private static final double GOOD_SAVINGS_RATE = 20.0;

    /**
     * Returns the authenticated user.
     *
     * @param email Authenticated user's email.
     * @return User entity.
     */
    private User getUserByEmail(
            String email
    ) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: " + email
                        ));
    }

    /**
     * Builds the profile response.
     *
     * @param user User entity.
     * @return Profile response.
     */
    private ProfileResponse buildProfileResponse(
            User user
    ) {

        return ProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .monthlyIncome(user.getMonthlyIncome())
                .riskProfile(user.getRiskProfile())
                .currency(user.getCurrency())
                .build();
    }

    /**
     * Calculates total expenses.
     *
     * @param expenses User expenses.
     * @return Total expenses.
     */
    private double calculateTotalExpenses(
            List<Expense> expenses
    ) {

        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Calculates monthly savings.
     *
     * @param user User.
     * @param expenses User expenses.
     * @return Monthly savings.
     */
    private double calculateMonthlySavings(
            User user,
            List<Expense> expenses
    ) {

        return user.getMonthlyIncome()
                - calculateTotalExpenses(expenses);
    }

    /**
     * Calculates savings rate.
     *
     * @param user User.
     * @param expenses User expenses.
     * @return Savings rate.
     */
    private double calculateSavingsRate(
            User user,
            List<Expense> expenses
    ) {

        double monthlyIncome = user.getMonthlyIncome();

        if (monthlyIncome <= 0) {
            return 0.0;
        }

        double savingsRate =
                (calculateMonthlySavings(user, expenses)
                        / monthlyIncome) * 100;

        return Math.round(savingsRate * 100.0) / 100.0;
    }

    /**
     * Returns the authenticated user's profile.
     *
     * @param email Authenticated user's email.
     * @return User profile.
     */
    public ProfileResponse getProfile(
            String email
    ) {

        User user = getUserByEmail(email);

        return buildProfileResponse(user);
    }

    /**
     * Updates the authenticated user's profile.
     *
     * @param email Authenticated user's email.
     * @param request Updated profile details.
     * @return Updated profile.
     */
    public ProfileResponse updateProfile(
            String email,
            UpdateProfileRequest request
    ) {

        User user = getUserByEmail(email);

        user.setMonthlyIncome(request.getMonthlyIncome());
        user.setRiskProfile(request.getRiskProfile());
        user.setCurrency(request.getCurrency());

        User updatedUser =
                userRepository.save(user);

        return buildProfileResponse(updatedUser);
    }

    /**
     * Calculates the user's financial health.
     *
     * @param email Authenticated user's email.
     * @return Financial health summary.
     */
    public FinancialHealthResponse getFinancialHealth(
            String email
    ) {

        User user = getUserByEmail(email);

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        double monthlyIncome =
                user.getMonthlyIncome();

        double totalExpenses =
                calculateTotalExpenses(expenses);

        double monthlySavings =
                calculateMonthlySavings(user, expenses);

        double savingsRate =
                calculateSavingsRate(user, expenses);

        FinancialHealthStatus status;

        if (savingsRate >= EXCELLENT_SAVINGS_RATE) {

            status = FinancialHealthStatus.EXCELLENT;

        } else if (savingsRate >= GOOD_SAVINGS_RATE) {

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