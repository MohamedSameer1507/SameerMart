package com.sameer.sameermart.servlet;

import com.sameer.sameermart.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChatServletTest {

    private ChatServlet servlet;
    private ChatService mockChatService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new ChatServlet();
        mockChatService = mock(ChatService.class);
        servlet.setChatService(mockChatService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        responseWriter = new StringWriter();

        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("test-session-123");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testDoGetReturnsMethodNotAllowed() throws Exception {
        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":false"));
        assertTrue(json.contains("METHOD_NOT_ALLOWED"));
    }

    @Test
    public void testDoPostEmptyMessageReturnsBadRequest() throws Exception {
        when(request.getParameter("message")).thenReturn("");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":false"));
        assertTrue(json.contains("VALIDATION_ERROR"));
    }

    @Test
    public void testDoPostValidMessageReturnsOkEnvelope() throws Exception {
        when(request.getParameter("message")).thenReturn("How to order?");
        when(mockChatService.processMessage(eq("test-session-123"), eq("How to order?")))
                .thenReturn("Click Add to Cart and proceed to checkout.");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("\"reply\":\"Click Add to Cart and proceed to checkout.\""));
    }

    @Test
    public void testDoPostRateLimitReturns429() throws Exception {
        when(request.getParameter("message")).thenReturn("Hello spam");
        when(mockChatService.processMessage(anyString(), anyString()))
                .thenThrow(new IllegalStateException("Rate limit exceeded"));

        servlet.doPost(request, response);

        verify(response).setStatus(429);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":false"));
        assertTrue(json.contains("RATE_LIMIT_EXCEEDED"));
    }

    @Test
    public void testDoPostInvalidCsrfReturns403() throws Exception {
        when(session.getAttribute("csrfToken")).thenReturn("token-abc");
        when(request.getHeader("X-CSRF-Token")).thenReturn("token-xyz-invalid");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":false"));
        assertTrue(json.contains("CSRF_ERROR"));
    }
}
