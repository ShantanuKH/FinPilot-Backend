package com.shantanu.FinPilot.budget.respository;

import com.shantanu.FinPilot.budget.entity.Budget;
import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import com.shantanu.FinPilot.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

    Optional<Budget> findByUserAndCategoryAndMonth(
            User user,
            ExpenseCategory category,
            String month
    );

}