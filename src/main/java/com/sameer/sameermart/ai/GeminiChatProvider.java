package com.sameer.sameermart.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sameer.sameermart.util.JsonUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * GeminiChatProvider integrates with the Google Gemini API using java.net.http.HttpClient.
 * Applies domain restrictions, privacy guardrails (no PII), and automatic failover.
 */
public class GeminiChatProvider implements ChatProvider {

    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;
    private final MockChatProvider fallbackProvider;

    public GeminiChatProvider() {
        this(
                System.getenv("GEMINI_API_KEY"),
                System.getenv("GEMINI_MODEL") != null && !System.getenv("GEMINI_MODEL").trim().isEmpty()
                        ? System.getenv("GEMINI_MODEL").trim()
                        : "gemini-3.1-flash-lite"
        );
    }

    public GeminiChatProvider(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = (model == null || model.trim().isEmpty()) ? "gemini-3.1-flash-lite" : model.trim();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.fallbackProvider = new MockChatProvider();
    }

    @Override
    public String getReply(String userMessage, String context) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return fallbackProvider.getReply(userMessage, context);
        }

        try {
            String endpoint = String.format(
                    "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent",
                    model
            );

            // Construct sanitized prompt with system guardrails (NO PII)
            JsonObject requestBody = new JsonObject();

            // System instruction to restrict bot scope to SameerMart
            JsonObject sysInstruction = new JsonObject();
            JsonArray sysParts = new JsonArray();
            JsonObject sysTextPart = new JsonObject();
            sysTextPart.addProperty(
                    "text",
                    "You are SameerMart Assistant, an AI customer support bot for the SameerMart e-commerce marketplace. " +
                            "You answer questions strictly related to SameerMart products, ordering, mock payments, shipping, " +
                            "seller product listings, customer reviews, and order tracking. " +
                            "Refuse any off-topic queries politely and redirect back to SameerMart. " +
                            "Never ask for or display passwords, personal financial details, or credentials."
            );
            sysParts.add(sysTextPart);
            sysInstruction.add("parts", sysParts);
            requestBody.add("systemInstruction", sysInstruction);

            // User prompt + generic catalog context (no user PII)
            StringBuilder promptBuilder = new StringBuilder();
            if (context != null && !context.trim().isEmpty()) {
                promptBuilder.append("Context on available products: ").append(context).append("\n\n");
            }
            promptBuilder.append("User query: ").append(userMessage);

            JsonArray contents = new JsonArray();
            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("role", "user");
            JsonArray parts = new JsonArray();
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", promptBuilder.toString());
            parts.add(textPart);
            contentObj.add("parts", parts);
            contents.add(contentObj);
            requestBody.add("contents", contents);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .timeout(Duration.ofSeconds(8))
                    .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(requestBody)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                    JsonObject content = firstCandidate.getAsJsonObject("content");
                    if (content != null) {
                        JsonArray replyParts = content.getAsJsonArray("parts");
                        if (replyParts != null && !replyParts.isEmpty()) {
                            String replyText = replyParts.get(0).getAsJsonObject().get("text").getAsString();
                            if (replyText != null && !replyText.trim().isEmpty()) {
                                return replyText.trim();
                            }
                        }
                    }
                }
            }

            // Fallback on non-200, 429, or unexpected response format
            return fallbackProvider.getReply(userMessage, context);

        } catch (Exception e) {
            // Log without revealing API key or sensitive data, then fallback safely
            return fallbackProvider.getReply(userMessage, context);
        }
    }
}
