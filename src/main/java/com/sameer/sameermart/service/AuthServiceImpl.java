package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.UserDAO;
import com.sameer.sameermart.exception.ConflictException;
import com.sameer.sameermart.exception.UnauthorizedException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.User;

public class AuthServiceImpl implements AuthService {

    private final UserDAO userDAO;

    public AuthServiceImpl() {
        this.userDAO = new UserDAO();
    }

    public AuthServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new ValidationException("Email and password are required");
        }
        User user = userDAO.findByEmail(email.trim());
        if (user == null || !org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return user;
    }

    @Override
    public User register(String name, String email, String password, String role) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (password == null || password.trim().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
        if (role == null || (!"BUYER".equalsIgnoreCase(role) && !"SELLER".equalsIgnoreCase(role))) {
            throw new ValidationException("Role must be either BUYER or SELLER");
        }

        if (isEmailTaken(email.trim())) {
            throw new ConflictException("Email already registered: " + email.trim());
        }

        User newUser = new User(name.trim(), email.trim(), password, role.trim().toUpperCase());
        boolean registered = userDAO.registerUser(newUser);
        if (!registered) {
            throw new RuntimeException("Failed to register user");
        }
        return userDAO.findByEmail(email.trim());
    }

    @Override
    public boolean isEmailTaken(String email) {
        if (email == null) {
            return false;
        }
        return userDAO.findByEmail(email.trim()) != null;
    }
}
