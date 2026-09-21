package com.sameer.sameermart.user;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("An account already exists with this email address.");
    }
}
