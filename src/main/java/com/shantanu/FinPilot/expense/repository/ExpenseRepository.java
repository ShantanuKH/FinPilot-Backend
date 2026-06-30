package com.shantanu.FinPilot.expense.repository;

import com.shantanu.FinPilot.expense.entity.Expense;
import com.shantanu.FinPilot.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);
}
