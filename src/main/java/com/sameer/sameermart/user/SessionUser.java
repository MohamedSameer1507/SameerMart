package com.sameer.sameermart.user;

import java.io.Serializable;

/** Data deliberately kept in the HTTP session; password hashes never enter the session. */
public record SessionUser(long id, String name, String email, UserRole role) implements Serializable {
    public static final String ATTRIBUTE = "authenticatedUser";
}
