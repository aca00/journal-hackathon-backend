package com.hack.journal.exception;

public class UserNotVerifiedException extends RuntimeException {
    @Override
    public String toString() {
        return "User not enabled. Verify by generating OTP";
    }
}
