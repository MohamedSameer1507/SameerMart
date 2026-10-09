package com.sameer.sameermart.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MockChatProviderTest {

    private MockChatProvider provider;

    @BeforeEach
    public void setUp() {
        provider = new MockChatProvider();
    }

    @Test
    public void testHowToOrder() {
        String reply = provider.getReply("How to order a product?", null);
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("cart"));
        assertTrue(reply.toLowerCase().contains("checkout"));
    }

    @Test
    public void testMockPaymentInfo() {
        String reply = provider.getReply("What payment methods do you support?", null);
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("mock"));
        assertTrue(reply.toLowerCase().contains("payment"));
    }

    @Test
    public void testReturnPolicy() {
        String reply = provider.getReply("What is your refund and return policy?", null);
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("return"));
    }

    @Test
    public void testSellerListing() {
        String reply = provider.getReply("How can a seller list products?", null);
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("seller"));
        assertTrue(reply.toLowerCase().contains("add product"));
    }

    @Test
    public void testCategoriesAvailable() {
        String reply = provider.getReply("What categories are available?", null);
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("spices") || reply.toLowerCase().contains("categories"));
    }

    @Test
    public void testReviewsRules() {
        String reply = provider.getReply("How do reviews and ratings work?", null);
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("delivered"));
        assertTrue(reply.toLowerCase().contains("review"));
    }

    @Test
    public void testOrderStatusWorkflow() {
        String reply = provider.getReply("What does my order status mean?", null);
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("pending"));
        assertTrue(reply.toLowerCase().contains("confirmed"));
        assertTrue(reply.toLowerCase().contains("delivered"));
    }

    @Test
    public void testCustomerSupportContact() {
        String reply = provider.getReply("Who can I contact for support?", null);
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("support@sameermart.com"));
    }

    @Test
    public void testShippingInfo() {
        String reply = provider.getReply("How does shipping work?", null);
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("delivery") || reply.toLowerCase().contains("shipping") || reply.toLowerCase().contains("shipped"));
    }

    @Test
    public void testAccountRegistration() {
        String reply = provider.getReply("How to register a new account?", null);
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("buyer") || reply.toLowerCase().contains("register"));
    }

    @Test
    public void testGeneralGreetingAndFallback() {
        String reply = provider.getReply("Hello", null);
        assertNotNull(reply);
        assertTrue(reply.contains("SameerMart"));
    }
}
