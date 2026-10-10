package com.sameer.sameermart.util;

public class HtmlUtil {

    private HtmlUtil() {
        // Utility class
    }

    /**
     * Safely escapes HTML special characters to prevent Cross-Site Scripting (XSS).
     *
     * @param input Raw user-supplied string
     * @return Escaped string safe for HTML rendering, or empty string if null
     */
    public static String escape(String input) {
        if (input == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder(input.length() + 16);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&':
                    escaped.append("&amp;");
                    break;
                case '<':
                    escaped.append("&lt;");
                    break;
                case '>':
                    escaped.append("&gt;");
                    break;
                case '"':
                    escaped.append("&quot;");
                    break;
                case '\'':
                    escaped.append("&#x27;");
                    break;
                default:
                    escaped.append(c);
                    break;
            }
        }
        return escaped.toString();
    }

    /**
     * Safely resolves a product image URL for rendering in HTML views.
     *
     * @param imageUrl Raw image URL or filename stored in the database
     * @param contextPath Web application servlet context path (e.g. "/sameermart")
     * @return Fully resolved web path or external HTTP/HTTPS URL
     */
    public static String resolveImageUrl(String imageUrl, String contextPath) {
        String base = (contextPath == null) ? "" : contextPath;
        if (imageUrl == null || imageUrl.trim().isEmpty() || "default-product.svg".equalsIgnoreCase(imageUrl.trim())) {
            return base + "/images/default-product.svg";
        }
        String trimmed = imageUrl.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        if (trimmed.startsWith("default-")) {
            return base + "/images/" + trimmed;
        }
        return base + "/images/products/" + trimmed;
    }
}
