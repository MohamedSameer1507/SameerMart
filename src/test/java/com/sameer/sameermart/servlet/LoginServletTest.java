package com.sameer.sameermart.servlet;

import com.sameer.sameermart.dao.UserDAO;
import com.sameer.sameermart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

public class LoginServletTest {

    private LoginServlet servlet;
    private UserDAO mockUserDAO;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new LoginServlet();
        mockUserDAO = mock(UserDAO.class);

        Field userDAOField = LoginServlet.class.getDeclaredField("userDAO");
        userDAOField.setAccessible(true);
        userDAOField.set(servlet, mockUserDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getContextPath()).thenReturn("/SameerMart");
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testGetRedirectsToLoginPage() throws Exception {
        servlet.doGet(request, response);
        verify(response).sendRedirect("/SameerMart/login.jsp");
    }

    @Test
    public void testPostMissingCredentialsRedirectsWithError() throws Exception {
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("password")).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/SameerMart/login.jsp?error=Please+enter+email+and+password");
        verifyNoInteractions(mockUserDAO);
    }

    @Test
    public void testPostUserNotFoundRedirectsWithError() throws Exception {
        when(request.getParameter("email")).thenReturn("unknown@test.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(mockUserDAO.findByEmail("unknown@test.com")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/SameerMart/login.jsp?error=Invalid+email+or+password");
    }

    @Test
    public void testPostIncorrectPasswordRedirectsWithError() throws Exception {
        User user = new User(1, "Alice", "alice@test.com", "$2a$12$hashed", "BUYER");
        when(request.getParameter("email")).thenReturn("alice@test.com");
        when(request.getParameter("password")).thenReturn("wrongpass");
        when(mockUserDAO.findByEmail("alice@test.com")).thenReturn(user);
        when(mockUserDAO.verifyPassword("wrongpass", "$2a$12$hashed")).thenReturn(false);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/SameerMart/login.jsp?error=Invalid+email+or+password");
        verify(session, never()).setAttribute(eq("user"), any());
    }

    @Test
    public void testPostSuccessfulBuyerLoginSetsSessionAndFixationDefense() throws Exception {
        User user = new User(42, "Bob Buyer", "bob@test.com", "$2a$12$hashed", "BUYER");
        when(request.getParameter("email")).thenReturn("bob@test.com");
        when(request.getParameter("password")).thenReturn("correctpass");
        when(mockUserDAO.findByEmail("bob@test.com")).thenReturn(user);
        when(mockUserDAO.verifyPassword("correctpass", "$2a$12$hashed")).thenReturn(true);

        servlet.doPost(request, response);

        // Verify session fixation mitigation
        verify(request).changeSessionId();

        // Verify session attributes
        verify(session).setAttribute("user", user);
        verify(session).setAttribute("userId", 42);
        verify(session).setAttribute("userName", "Bob Buyer");
        verify(session).setAttribute("userEmail", "bob@test.com");
        verify(session).setAttribute("role", "BUYER");

        // Verify redirect to buyer marketplace
        verify(response).sendRedirect("/SameerMart/buyer/marketplace");
    }

    @Test
    public void testPostSuccessfulSellerLoginRedirectsToSellerDashboard() throws Exception {
        User user = new User(99, "Sam Seller", "seller@test.com", "$2a$12$hashed", "SELLER");
        when(request.getParameter("email")).thenReturn("seller@test.com");
        when(request.getParameter("password")).thenReturn("pass");
        when(mockUserDAO.findByEmail("seller@test.com")).thenReturn(user);
        when(mockUserDAO.verifyPassword("pass", "$2a$12$hashed")).thenReturn(true);

        servlet.doPost(request, response);

        verify(session).setAttribute("role", "SELLER");
        verify(response).sendRedirect("/SameerMart/seller/dashboard.jsp");
    }

    @Test
    public void testPostSuccessfulAdminLoginRedirectsToAdminDashboard() throws Exception {
        User user = new User(1, "Root Admin", "admin@test.com", "$2a$12$hashed", "ADMIN");
        when(request.getParameter("email")).thenReturn("admin@test.com");
        when(request.getParameter("password")).thenReturn("adminpass");
        when(mockUserDAO.findByEmail("admin@test.com")).thenReturn(user);
        when(mockUserDAO.verifyPassword("adminpass", "$2a$12$hashed")).thenReturn(true);

        servlet.doPost(request, response);

        verify(session).setAttribute("role", "ADMIN");
        verify(response).sendRedirect("/SameerMart/admin/dashboard.jsp");
    }

    @Test
    public void testLoginRateLimitingAfterExcessiveFailures() throws Exception {
        when(request.getParameter("email")).thenReturn("attacker@test.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(request.getRemoteAddr()).thenReturn("192.168.1.50");
        when(mockUserDAO.findByEmail("attacker@test.com")).thenReturn(null);

        // 5 consecutive failed attempts
        for (int i = 0; i < 5; i++) {
            servlet.doPost(request, response);
        }
        verify(response, times(5)).sendRedirect("/SameerMart/login.jsp?error=Invalid+email+or+password");
        verify(mockUserDAO, times(5)).findByEmail("attacker@test.com");

        // 6th attempt should be blocked immediately by sliding-window rate limiter
        reset(response);
        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("Too+many+failed+login+attempts"));
        // UserDAO must NOT be queried for the 6th attempt (CPU/DB exhaustion defense)
        verify(mockUserDAO, times(5)).findByEmail("attacker@test.com");
    }

    @Test
    public void testSuccessfulLoginResetsFailedAttemptCounter() throws Exception {
        User user = new User(10, "Recovered User", "user@test.com", "$2a$12$hashed", "BUYER");
        when(request.getRemoteAddr()).thenReturn("192.168.1.55");

        // 3 failed attempts
        when(request.getParameter("email")).thenReturn("user@test.com");
        when(request.getParameter("password")).thenReturn("badpass");
        when(mockUserDAO.findByEmail("user@test.com")).thenReturn(user);
        when(mockUserDAO.verifyPassword("badpass", "$2a$12$hashed")).thenReturn(false);

        for (int i = 0; i < 3; i++) {
            servlet.doPost(request, response);
        }
        verify(response, times(3)).sendRedirect("/SameerMart/login.jsp?error=Invalid+email+or+password");

        // 4th attempt: successful authentication resets the counter
        when(request.getParameter("password")).thenReturn("goodpass");
        when(mockUserDAO.verifyPassword("goodpass", "$2a$12$hashed")).thenReturn(true);
        reset(response);
        servlet.doPost(request, response);
        verify(response).sendRedirect("/SameerMart/buyer/marketplace");

        // 3 more failed attempts should not trigger lockout because counter was reset
        when(request.getParameter("password")).thenReturn("badpass");
        when(mockUserDAO.verifyPassword("badpass", "$2a$12$hashed")).thenReturn(false);
        reset(response);
        for (int i = 0; i < 3; i++) {
            servlet.doPost(request, response);
        }
        verify(response, times(3)).sendRedirect("/SameerMart/login.jsp?error=Invalid+email+or+password");
    }
}
