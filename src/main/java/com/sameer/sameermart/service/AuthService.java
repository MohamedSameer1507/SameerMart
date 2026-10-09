package com.sameer.sameermart.service;

import com.sameer.sameermart.model.User;

public interface AuthService {
    User authenticate(String email, String password);
    User register(String name, String email, String password, String role);
    boolean isEmailTaken(String email);
}
