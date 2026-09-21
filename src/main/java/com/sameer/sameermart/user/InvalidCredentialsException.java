package com.sameer.sameermart.user;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email or password is incorrect.");
    }
}
