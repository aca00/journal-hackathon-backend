package com.hack.journal.exception;

public class UserAlreadyCreatedException extends RuntimeException {
    public UserAlreadyCreatedException(String message) {
        super(message);
    }
}
