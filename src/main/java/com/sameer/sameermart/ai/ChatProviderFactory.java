package com.sameer.sameermart.ai;

/**
 * Factory pattern resolving ChatProvider based on configuration or environment variable.
 */
public class ChatProviderFactory {

    public static ChatProvider getProvider() {
        String providerConfig = System.getenv("AI_CHATBOT_PROVIDER");
        if (providerConfig == null || providerConfig.trim().isEmpty()) {
            providerConfig = System.getProperty("ai.chatbot.provider", "mock");
        }

        if ("gemini".equalsIgnoreCase(providerConfig.trim())) {
            return new GeminiChatProvider();
        }

        return new MockChatProvider();
    }

    public static ChatProvider getProvider(String providerName) {
        if ("gemini".equalsIgnoreCase(providerName)) {
            return new GeminiChatProvider();
        }
        return new MockChatProvider();
    }
}
