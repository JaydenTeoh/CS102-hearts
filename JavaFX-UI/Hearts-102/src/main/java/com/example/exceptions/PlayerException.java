package com.example.exceptions;

public class PlayerException extends RuntimeException {
    public PlayerException(String errorMessage) {
        super(errorMessage);
    }
}
