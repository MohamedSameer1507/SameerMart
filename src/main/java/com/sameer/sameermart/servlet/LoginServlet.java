package com.sameer.sameermart.servlet;

import com.sameer.sameermart.dao.UserDAO;
import com.sameer.sameermart.model.User;
import com.sameer.sameermart.util.CsrfUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 5 * 60 * 1000L; // 5 minutes

    // Rate limiter: client key (IP + ":" + email) -> deque of failed attempt timestamps
    private final Map<String, Deque<Long>> failedAttempts = new ConcurrentHashMap<>();

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    private synchronized boolean isLockedOut(String key) {
        long now = System.currentTimeMillis();
        Deque<Long> attempts = failedAttempts.get(key);
        if (attempts == null) {
            return false;
        }

        while (!attempts.isEmpty() && (now - attempts.peekFirst() > LOCKOUT_DURATION_MS)) {
            attempts.pollFirst();
        }

        return attempts.size() >= MAX_FAILED_ATTEMPTS;
    }

    private synchronized void recordFailedAttempt(String key) {
        long now = System.currentTimeMillis();
        Deque<Long> attempts = failedAttempts.computeIfAbsent(key, k -> new ArrayDeque<>());

        while (!attempts.isEmpty() && (now - attempts.peekFirst() > LOCKOUT_DURATION_MS)) {
            attempts.pollFirst();
        }

        attempts.addLast(now);
    }

    private synchronized void clearFailedAttempts(String key) {
        failedAttempts.remove(key);
    }

    void resetAttempts() {
        failedAttempts.clear();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(
                request.getContextPath() + "/login.jsp"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        /*
         * -----------------------------
         * VALIDATION
         * -----------------------------
         */

        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/login.jsp?error=Please+enter+email+and+password"
            );

            return;
        }

        email = email.trim();

        /*
         * -----------------------------
         * RATE LIMITING (BRUTE FORCE DEFENSE)
         * -----------------------------
         */

        String remoteIp = request.getRemoteAddr();
        String clientIp = (remoteIp != null && !remoteIp.trim().isEmpty()) ? remoteIp.trim() : "127.0.0.1";
        String attemptKey = clientIp + ":" + email.toLowerCase(Locale.ROOT);

        if (isLockedOut(attemptKey)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/login.jsp?error=Too+many+failed+login+attempts.+Please+try+again+in+5+minutes."
            );
            return;
        }

        /*
         * -----------------------------
         * FIND USER
         * -----------------------------
         */

        User user = userDAO.findByEmail(email);

        if (user == null) {
            recordFailedAttempt(attemptKey);

            response.sendRedirect(
                    request.getContextPath()
                            + "/login.jsp?error=Invalid+email+or+password"
            );

            return;
        }

        /*
         * -----------------------------
         * VERIFY PASSWORD
         * -----------------------------
         */

        boolean passwordCorrect =
                userDAO.verifyPassword(
                        password,
                        user.getPassword()
                );

        if (!passwordCorrect) {
            recordFailedAttempt(attemptKey);

            response.sendRedirect(
                    request.getContextPath()
                            + "/login.jsp?error=Invalid+email+or+password"
            );

            return;
        }

        // Authentication succeeded — reset failed attempts for this client key
        clearFailedAttempts(attemptKey);

        /*
         * -----------------------------
         * CREATE SESSION
         * -----------------------------
         *
         * IMPORTANT:
         *
         * "user" stores the complete User object.
         *
         * "userName", "userEmail", "role"
         * store individual values.
         */

        HttpSession session = request.getSession();

        // Prevent Session Fixation by regenerating the session identifier upon authentication
        try {
            request.changeSessionId();
        } catch (Exception ignored) {
        }

        session.setAttribute("user", user);

        session.setAttribute(
                "userId",
                user.getId()
        );

        session.setAttribute(
                "userName",
                user.getName()
        );

        session.setAttribute(
                "userEmail",
                user.getEmail()
        );

        session.setAttribute(
                "role",
                user.getRole()
        );

        session.setAttribute(
                "userRole",
                user.getRole()
        );

        CsrfUtil.getOrCreateToken(session);

        /*
         * -----------------------------
         * ROLE-BASED REDIRECT
         * -----------------------------
         */

        String role = user.getRole();

        if ("BUYER".equalsIgnoreCase(role)) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/buyer/marketplace"
            );

        } else if ("SELLER".equalsIgnoreCase(role)) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/seller/dashboard.jsp"
            );

        } else if ("ADMIN".equalsIgnoreCase(role)) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/dashboard.jsp"
            );

        } else {

            session.invalidate();

            response.sendRedirect(
                    request.getContextPath()
                            + "/login.jsp?error=Invalid+user+role"
            );
        }
    }
}