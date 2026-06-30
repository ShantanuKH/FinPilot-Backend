package com.shantanu.FinPilot.common.exception;

public class ExpenseNotFoundException
        extends RuntimeException {

    public ExpenseNotFoundException(
            String message
    ) {
        super(message);
    }
}