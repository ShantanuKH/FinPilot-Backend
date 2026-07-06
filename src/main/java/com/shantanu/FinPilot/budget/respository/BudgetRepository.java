package com.shantanu.FinPilot.budget.respository;

import com.shantanu.FinPilot.budget.entity.Budget;
import com.shantanu.FinPilot.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

}