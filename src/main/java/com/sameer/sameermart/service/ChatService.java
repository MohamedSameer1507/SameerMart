package com.sameer.sameermart.service;

import com.sameer.sameermart.ai.ChatProvider;
import com.sameer.sameermart.ai.ChatProviderFactory;
import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.model.Product;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ChatService coordinates AI chatbot requests, enforcing input validation,
 * per-session rate limiting (10 msg/min), query caching, catalog context enrichment,
 * and failover resilience.
 */
public class ChatService {

    private static final int MAX_MESSAGE_LENGTH = 500;
    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final long ONE_MINUTE_MILLIS = 60_000L;
    private static final String STATIC_DEGRADED_REPLY =
            "Our assistant is temporarily unavailable. You can browse our marketplace directly or reach support@sameermart.com.";

    private final ChatProvider chatProvider;
    private final ProductDAO productDAO;

    // Per-session rate limiter: sessionId -> queue of request timestamps
    private final Map<String, Deque<Long>> sessionTimestamps = new ConcurrentHashMap<>();

    // Per-session response cache: sessionId -> (query -> reply)
    private final Map<String, Map<String, String>> sessionCache = new ConcurrentHashMap<>();

    public ChatService() {
        this(ChatProviderFactory.getProvider(), new ProductDAO());
    }

    public ChatService(ChatProvider chatProvider, ProductDAO productDAO) {
        this.chatProvider = chatProvider;
        this.productDAO = productDAO;
    }

    /**
     * Processes an incoming chat message for a given user session.
     *
     * @param sessionId the HTTP session ID
     * @param message the raw customer message
     * @return reply string
     * @throws IllegalArgumentException if message is invalid
     * @throws IllegalStateException if rate limit is exceeded
     */
    public String processMessage(String sessionId, String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty.");
        }

        String trimmed = message.trim();
        if (trimmed.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message exceeds maximum length of " + MAX_MESSAGE_LENGTH + " characters.");
        }

        String effectiveSessionId = (sessionId != null && !sessionId.trim().isEmpty()) ? sessionId : "anonymous-session";

        // Check and enforce rate limit (10 requests/minute per session)
        enforceRateLimit(effectiveSessionId);

        // Check in-memory per-session cache for identical queries
        String cacheKey = trimmed.toLowerCase(Locale.ROOT);
        Map<String, String> userCache = sessionCache.computeIfAbsent(effectiveSessionId, k -> new ConcurrentHashMap<>());
        if (userCache.containsKey(cacheKey)) {
            return userCache.get(cacheKey);
        }

        // Build domain context from catalog if relevant
        String context = resolveCatalogContext(trimmed);

        String reply;
        try {
            reply = chatProvider.getReply(trimmed, context);
            if (reply == null || reply.trim().isEmpty()) {
                reply = STATIC_DEGRADED_REPLY;
            }
        } catch (Exception e) {
            // Failover gracefully — user must never see an unhandled error
            reply = STATIC_DEGRADED_REPLY;
        }

        userCache.put(cacheKey, reply);
        return reply;
    }

    private synchronized void enforceRateLimit(String sessionId) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = sessionTimestamps.computeIfAbsent(sessionId, k -> new ArrayDeque<>());

        // Evict timestamps older than 1 minute
        while (!timestamps.isEmpty() && (now - timestamps.peekFirst() > ONE_MINUTE_MILLIS)) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= MAX_REQUESTS_PER_MINUTE) {
            throw new IllegalStateException("Rate limit exceeded: maximum 10 messages per minute.");
        }

        timestamps.addLast(now);
    }

    private String resolveCatalogContext(String query) {
        try {
            if (productDAO == null) {
                return "";
            }
            List<Product> products = productDAO.findAllAvailable();
            if (products == null || products.isEmpty()) {
                return "";
            }

            return products.stream()
                    .limit(5)
                    .map(p -> String.format("%s (%s - $%.2f)", p.getName(), p.getCategory(), p.getPrice()))
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "";
        }
    }

    public void clearCache(String sessionId) {
        if (sessionId != null) {
            sessionCache.remove(sessionId);
            sessionTimestamps.remove(sessionId);
        }
    }
}
