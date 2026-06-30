package com.shantanu.FinPilot.common.exception;

public class UnauthorizedExpenseAccessException
        extends RuntimeException {

    public UnauthorizedExpenseAccessException(
            String message
    ) {
        super(message);
    }
}