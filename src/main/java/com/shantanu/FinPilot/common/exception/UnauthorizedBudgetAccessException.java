package com.shantanu.FinPilot.common.exception;

public class UnauthorizedBudgetAccessException
        extends RuntimeException {

    public UnauthorizedBudgetAccessException(
            String message
    ) {
        super(message);
    }
}