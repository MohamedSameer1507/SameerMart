package com.sameer.sameermart.servlet.api;

import com.sameer.sameermart.dao.OrderDAO;
import com.sameer.sameermart.dto.ApiResponse;
import com.sameer.sameermart.model.User;
import com.sameer.sameermart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * REST API endpoint: GET /api/v1/orders
 * Returns role-aware orders list:
 * - BUYER: Returns orders placed by the current buyer
 * - SELLER: Returns customer line items ordered from the current seller
 * - ADMIN: Returns all orders on the platform
 */
@WebServlet(name = "OrderApiServlet", urlPatterns = {"/api/v1/orders"})
public class OrderApiServlet extends HttpServlet {

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("UNAUTHORIZED", "Authentication required")));
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole() != null ? user.getRole().toUpperCase() : "";

        Object ordersData;
        switch (role) {
            case "BUYER" -> ordersData = orderDAO.findOrdersByBuyerId(user.getId());
            case "SELLER" -> ordersData = orderDAO.findSellerOrders(user.getId());
            case "ADMIN" -> ordersData = orderDAO.findAllOrders();
            default -> {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(JsonUtil.toJson(ApiResponse.error("FORBIDDEN", "Unknown user role")));
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtil.toJson(ApiResponse.ok(ordersData)));
    }
}
