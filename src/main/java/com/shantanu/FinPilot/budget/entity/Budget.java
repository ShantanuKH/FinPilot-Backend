package com.shantanu.FinPilot.budget.entity;

import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import com.shantanu.FinPilot.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Column(nullable = false)
    private Double budgetAmount;

    @Column(nullable = false)
    private String month;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}