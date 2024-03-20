package com.example.exceptions;

public class TooManyPlayersException extends Exception {
    public TooManyPlayersException(String errorMessage) {
        super(errorMessage);
    }
}