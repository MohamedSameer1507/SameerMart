package com.sameer.sameermart.servlet;

import com.sameer.sameermart.dao.OrderDAO;
import com.sameer.sameermart.dto.ApiResponse;
import com.sameer.sameermart.model.Order;
import com.sameer.sameermart.model.User;
import com.sameer.sameermart.util.CsrfUtil;
import com.sameer.sameermart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * OrderStatusServlet enables sellers and administrators to advance order statuses
 * through the lifecycle: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED (or CANCELLED).
 * Rejects invalid transitions with HTTP 409 Conflict.
 */
@WebServlet(name = "OrderStatusServlet", urlPatterns = {"/seller/order-status", "/admin/order-status", "/order/status"})
public class OrderStatusServlet extends HttpServlet {

    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.orderDAO = new OrderDAO();
    }

    public void setOrderDAO(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            respondError(request, response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHENTICATED", "Authentication required");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole();
        if (role == null || (!"SELLER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role))) {
            respondError(request, response, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "Seller or Admin role required");
            return;
        }

        if (!CsrfUtil.isValid(request)) {
            respondError(request, response, HttpServletResponse.SC_FORBIDDEN, "CSRF_ERROR", "Invalid or missing CSRF token");
            return;
        }

        String orderIdText = request.getParameter("orderId");
        String targetStatus = request.getParameter("status");

        if (orderIdText == null || orderIdText.trim().isEmpty() || targetStatus == null || targetStatus.trim().isEmpty()) {
            respondError(request, response, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", "Order ID and status are required");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdText.trim());
        } catch (NumberFormatException e) {
            respondError(request, response, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR", "Invalid order ID");
            return;
        }

        Order order = orderDAO.findOrderById(orderId);
        if (order == null) {
            respondError(request, response, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Order not found");
            return;
        }

        // Strict seller isolation: seller must own at least one item in this order
        if ("SELLER".equalsIgnoreCase(role) && !orderDAO.isSellerForOrder(user.getId(), orderId)) {
            respondError(request, response, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "You are not authorized to update this order");
            return;
        }

        String currentStatus = (order.getStatus() != null) ? order.getStatus().toUpperCase() : "CONFIRMED";
        String normalizedTarget = targetStatus.trim().toUpperCase();

        if (!isValidStatusTransition(currentStatus, normalizedTarget)) {
            respondError(request, response, 409, // 409 Conflict
                    "CONFLICT", String.format("Cannot transition order from %s to %s", currentStatus, normalizedTarget));
            return;
        }

        boolean updated = orderDAO.updateOrderStatus(orderId, normalizedTarget);
        if (!updated) {
            respondError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Failed to update order status");
            return;
        }

        // Return JSON if requested, else redirect
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", orderId);
            data.put("status", normalizedTarget);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.ok(data)));
            response.getWriter().flush();
        } else {
            String redirectUrl = "ADMIN".equalsIgnoreCase(role)
                    ? request.getContextPath() + "/admin/dashboard.jsp?success="
                    : request.getContextPath() + "/seller/orders?success=";
            String msg = URLEncoder.encode("Order #" + orderId + " status updated to " + normalizedTarget, StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl + msg);
        }
    }

    public static boolean isValidStatusTransition(String current, String target) {
        if (current == null || target == null) {
            return false;
        }
        if (current.equalsIgnoreCase(target)) {
            return true;
        }

        return switch (current) {
            case "PENDING" -> "CONFIRMED".equals(target) || "CANCELLED".equals(target);
            case "CONFIRMED" -> "SHIPPED".equals(target) || "CANCELLED".equals(target);
            case "SHIPPED" -> "DELIVERED".equals(target) || "CANCELLED".equals(target);
            default -> false; // DELIVERED and CANCELLED cannot transition further
        };
    }

    private void respondError(HttpServletRequest request, HttpServletResponse response, int statusCode, String code, String message)
            throws IOException {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            response.setStatus(statusCode);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error(code, message)));
            response.getWriter().flush();
        } else {
            response.sendError(statusCode, message);
        }
    }
}
