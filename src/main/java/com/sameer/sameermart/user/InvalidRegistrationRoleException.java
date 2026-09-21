package com.sameer.sameermart.user;

public class InvalidRegistrationRoleException extends RuntimeException {
    public InvalidRegistrationRoleException() {
        super("Administrator accounts cannot be created through public registration.");
    }
}
