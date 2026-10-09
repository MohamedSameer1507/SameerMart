package com.sameer.sameermart.servlet.api;

import com.sameer.sameermart.dao.CartDAO;
import com.sameer.sameermart.dto.ApiResponse;
import com.sameer.sameermart.model.CartItem;
import com.sameer.sameermart.model.User;
import com.sameer.sameermart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API endpoint: GET /api/v1/cart
 * Requires authenticated BUYER session. Returns items, total items, and grand total.
 */
@WebServlet(name = "CartApiServlet", urlPatterns = {"/api/v1/cart"})
public class CartApiServlet extends HttpServlet {

    private CartDAO cartDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.cartDAO = new CartDAO();
    }

    public void setCartDAO(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
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
        if (user.getRole() == null || !"BUYER".equalsIgnoreCase(user.getRole())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("FORBIDDEN", "Buyer role required")));
            return;
        }

        List<CartItem> items = cartDAO.getCartItems(user.getId());
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem item : items) {
            totalQuantity += item.getQuantity();
            if (item.getProduct() != null && item.getProduct().getPrice() != null) {
                BigDecimal itemSubtotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemSubtotal);
            }
        }

        Map<String, Object> cartData = new HashMap<>();
        cartData.put("items", items);
        cartData.put("itemCount", items.size());
        cartData.put("totalQuantity", totalQuantity);
        cartData.put("totalAmount", totalAmount);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtil.toJson(ApiResponse.ok(cartData)));
    }
}
