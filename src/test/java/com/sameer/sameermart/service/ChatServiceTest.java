package com.sameer.sameermart.service;

import com.sameer.sameermart.ai.ChatProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChatServiceTest {

    private ChatProvider mockProvider;
    private ChatService chatService;

    @BeforeEach
    public void setUp() {
        mockProvider = Mockito.mock(ChatProvider.class);
        chatService = new ChatService(mockProvider, null);
    }

    @Test
    public void testEmptyMessageThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> chatService.processMessage("session1", "   "));
    }

    @Test
    public void testMessageOver500CharsThrowsException() {
        String longMessage = "a".repeat(501);
        assertThrows(IllegalArgumentException.class, () -> chatService.processMessage("session1", longMessage));
    }

    @Test
    public void testValidMessageReturnsReply() {
        when(mockProvider.getReply(anyString(), anyString())).thenReturn("This is a canned reply.");

        String reply = chatService.processMessage("session1", "How do I buy?");
        assertEquals("This is a canned reply.", reply);
        verify(mockProvider, times(1)).getReply(anyString(), anyString());
    }

    @Test
    public void testDuplicateMessageUsesCache() {
        when(mockProvider.getReply(anyString(), anyString())).thenReturn("Cached response.");

        String reply1 = chatService.processMessage("session-cache-test", "Where is my order?");
        String reply2 = chatService.processMessage("session-cache-test", "Where is my order?");

        assertEquals("Cached response.", reply1);
        assertEquals("Cached response.", reply2);
        // Provider should only be called once because of in-memory caching
        verify(mockProvider, times(1)).getReply(anyString(), anyString());
    }

    @Test
    public void testRateLimitingEnforcedAt10PerMinute() {
        when(mockProvider.getReply(anyString(), anyString())).thenReturn("ok");

        String sessionId = "rate-limit-session";
        // Call 10 times with distinct messages so cache doesn't prevent checking
        for (int i = 1; i <= 10; i++) {
            chatService.processMessage(sessionId, "Message number " + i);
        }

        // 11th request must exceed rate limit and throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> chatService.processMessage(sessionId, "Message number 11"));
    }

    @Test
    public void testGracefulFallbackOnProviderException() {
        when(mockProvider.getReply(anyString(), anyString())).thenThrow(new RuntimeException("Simulated API failure"));

        String reply = chatService.processMessage("session-error-test", "Can you help me?");
        assertNotNull(reply);
        assertTrue(reply.contains("temporarily unavailable"));
    }
}
