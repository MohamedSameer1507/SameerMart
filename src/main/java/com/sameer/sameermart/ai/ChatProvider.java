package com.sameer.sameermart.ai;

/**
 * Common interface for chatbot providers (e.g. Gemini, Mock).
 */
public interface ChatProvider {
    /**
     * Generate a response to the user's message within the given marketplace context.
     *
     * @param userMessage the sanitized customer query
     * @param context optional product/catalog context
     * @return reply string
     */
    String getReply(String userMessage, String context);
}
