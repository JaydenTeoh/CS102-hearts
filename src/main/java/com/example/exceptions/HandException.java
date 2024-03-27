package com.example.exceptions;

public class HandException extends RuntimeException {
    public HandException(String errorMessage) {
        super(errorMessage);
    }
}