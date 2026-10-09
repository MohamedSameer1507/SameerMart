package com.sameer.sameermart.servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sameer.sameermart.dto.ApiResponse;
import com.sameer.sameermart.service.ChatService;
import com.sameer.sameermart.util.CsrfUtil;
import com.sameer.sameermart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ChatServlet exposes the AI chatbot endpoint at POST /api/v1/chat (and alias /api/chat).
 * Handles JSON/form payloads, CSRF verification, rate limiting, and standard envelope responses.
 */
@WebServlet(name = "ChatServlet", urlPatterns = {"/api/v1/chat", "/api/chat"})
public class ChatServlet extends HttpServlet {

    private ChatService chatService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.chatService = new ChatService();
    }

    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        sendJsonResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                ApiResponse.error("METHOD_NOT_ALLOWED", "GET is not supported for chat. Use POST."));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        String sessionToken = CsrfUtil.getOrCreateToken(session);

        // Validate CSRF if session already had one or if token was provided in header/param/body
        String submittedToken = request.getHeader("X-CSRF-Token");
        if (submittedToken == null || submittedToken.trim().isEmpty()) {
            submittedToken = request.getParameter("csrfToken");
        }

        // Read body to extract message (and csrfToken if in json)
        String userMessage = request.getParameter("message");
        if (userMessage == null) {
            String body = readRequestBody(request);
            if (body != null && !body.trim().isEmpty()) {
                try {
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    if (json.has("message")) {
                        userMessage = json.get("message").getAsString();
                    }
                    if (submittedToken == null && json.has("csrfToken")) {
                        submittedToken = json.get("csrfToken").getAsString();
                    }
                } catch (Exception ignored) {
                    // fall back to null userMessage
                }
            }
        }

        // If CSRF token is provided, verify it; if request explicitly claims a token that doesn't match, reject
        if (submittedToken != null && !submittedToken.trim().isEmpty()) {
            if (!CsrfUtil.constantTimeEquals(sessionToken, submittedToken.trim())) {
                sendJsonResponse(response, HttpServletResponse.SC_FORBIDDEN,
                        ApiResponse.error("CSRF_ERROR", "Invalid CSRF token."));
                return;
            }
        }

        if (userMessage == null || userMessage.trim().isEmpty()) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.error("VALIDATION_ERROR", "Message cannot be empty."));
            return;
        }

        try {
            String reply = chatService.processMessage(session.getId(), userMessage);
            Map<String, String> data = new HashMap<>();
            data.put("reply", reply);

            sendJsonResponse(response, HttpServletResponse.SC_OK, ApiResponse.ok(data));

        } catch (IllegalArgumentException e) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.error("VALIDATION_ERROR", e.getMessage()));
        } catch (IllegalStateException e) {
            sendJsonResponse(response, 429, // 429 Too Many Requests
                    ApiResponse.error("RATE_LIMIT_EXCEEDED", e.getMessage()));
        } catch (Exception e) {
            sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ApiResponse.error("SERVER_ERROR", "An unexpected error occurred."));
        }
    }

    private String readRequestBody(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            if (reader == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int statusCode, ApiResponse<?> body)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JsonUtil.toJson(body));
        response.getWriter().flush();
    }
}
