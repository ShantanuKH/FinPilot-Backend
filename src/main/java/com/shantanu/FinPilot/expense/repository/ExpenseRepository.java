package com.shantanu.FinPilot.expense.repository;

import com.shantanu.FinPilot.expense.entity.Expense;
import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import com.shantanu.FinPilot.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

//    To find user and implement pagination.
    Page<Expense> findByUser(
            User user,
            Pageable pageable
    );

//    To implement search
    Page<Expense> findByUserAndTitleContainingIgnoreCase(
            User user,
            String title,
            Pageable pageable
    );

    Page<Expense> findByUserAndCategory(
            User user,
            ExpenseCategory category,
            Pageable pageable
    );
}