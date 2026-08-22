package com.shantanu.FinPilot.common.exception;

public class UnauthorizedInvestmentAccessException extends RuntimeException {

    public UnauthorizedInvestmentAccessException(String message) {
        super(message);
    }
}