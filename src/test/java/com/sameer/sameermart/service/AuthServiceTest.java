package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.UserDAO;
import com.sameer.sameermart.exception.ConflictException;
import com.sameer.sameermart.exception.UnauthorizedException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private UserDAO mockUserDAO;
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        mockUserDAO = mock(UserDAO.class);
        authService = new AuthServiceImpl(mockUserDAO);
    }

    @Test
    public void testAuthenticateSuccess() {
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw("secret123", org.mindrot.jbcrypt.BCrypt.gensalt(12));
        User user = new User(1, "Test User", "test@test.com", hashed, "BUYER");
        when(mockUserDAO.findByEmail("test@test.com")).thenReturn(user);

        User authenticated = authService.authenticate("test@test.com", "secret123");
        assertNotNull(authenticated);
        assertEquals("test@test.com", authenticated.getEmail());
    }

    @Test
    public void testAuthenticateFailureThrowsUnauthorized() {
        when(mockUserDAO.findByEmail("test@test.com")).thenReturn(null);
        assertThrows(UnauthorizedException.class, () -> authService.authenticate("test@test.com", "wrong"));
    }

    @Test
    public void testAuthenticateEmptyCredentialsThrowsValidation() {
        assertThrows(ValidationException.class, () -> authService.authenticate("", ""));
    }

    @Test
    public void testRegisterSuccess() {
        when(mockUserDAO.registerUser(any(User.class))).thenReturn(true);
        User created = new User(2, "New", "new@test.com", "hash", "BUYER");
        when(mockUserDAO.findByEmail("new@test.com")).thenReturn(null).thenReturn(created);

        User registered = authService.register("New", "new@test.com", "password123", "BUYER");
        assertNotNull(registered);
        assertEquals("new@test.com", registered.getEmail());
    }

    @Test
    public void testRegisterDuplicateEmailThrowsConflict() {
        when(mockUserDAO.findByEmail("exists@test.com")).thenReturn(new User());
        assertThrows(ConflictException.class, () -> authService.register("User", "exists@test.com", "password123", "BUYER"));
    }

    @Test
    public void testRegisterInvalidRoleThrowsValidation() {
        assertThrows(ValidationException.class, () -> authService.register("User", "new@test.com", "password123", "SUPERADMIN"));
    }
}
