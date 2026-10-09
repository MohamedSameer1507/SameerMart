package com.sameer.sameermart.servlet;

import com.sameer.sameermart.model.User;
import com.sameer.sameermart.model.WishlistItem;
import com.sameer.sameermart.service.ServiceFactory;
import com.sameer.sameermart.service.WishlistService;
import com.sameer.sameermart.util.CsrfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(name = "WishlistServlet", urlPatterns = {"/buyer/wishlist", "/WishlistServlet"})
public class WishlistServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(WishlistServlet.class);

    private WishlistService wishlistService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.wishlistService = ServiceFactory.getWishlistService();
    }

    public void setWishlistService(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please+login+first");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (role == null || !"BUYER".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Buyer+access+required");
            return;
        }

        User user = (User) session.getAttribute("user");
        List<WishlistItem> wishlistItems = wishlistService.getWishlist(user.getId());

        request.setAttribute("wishlistItems", wishlistItems);
        request.getRequestDispatcher("/buyer/wishlist.jsp").forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please+login+first");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (role == null || !"BUYER".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Buyer+access+required");
            return;
        }

        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        String productIdParam = request.getParameter("productId");

        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/buyer/wishlist?error=Product+ID+missing");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(productIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/buyer/wishlist?error=Invalid+product+ID");
            return;
        }

        if ("add".equalsIgnoreCase(action)) {
            handleAdd(request, response, user.getId(), productId);
        } else if ("remove".equalsIgnoreCase(action)) {
            handleRemove(request, response, user.getId(), productId);
        } else if ("moveToCart".equalsIgnoreCase(action)) {
            handleMoveToCart(request, response, user.getId(), productId);
        } else {
            response.sendRedirect(request.getContextPath() + "/buyer/wishlist?error=Unknown+action");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response, int buyerId, int productId)
            throws IOException {
        try {
            wishlistService.addToWishlist(buyerId, productId);
            String redirect = request.getParameter("redirect");
            if (redirect != null && !redirect.trim().isEmpty() && redirect.startsWith("/")) {
                String safeRedirect = request.getContextPath() + redirect
                        + (redirect.contains("?") ? "&" : "?") + "success="
                        + URLEncoder.encode("Product added to wishlist", StandardCharsets.UTF_8);
                response.sendRedirect(safeRedirect);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/buyer/wishlist?success=Added+to+wishlist");
        } catch (Exception e) {
            logger.warn("Failed to add to wishlist for buyer: {}, product: {}", buyerId, productId, e);
            response.sendRedirect(request.getContextPath() + "/buyer/wishlist?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    private void handleRemove(HttpServletRequest request, HttpServletResponse response, int buyerId, int productId)
            throws IOException {
        try {
            wishlistService.removeFromWishlist(buyerId, productId);
            response.sendRedirect(request.getContextPath() + "/buyer/wishlist?success=Removed+from+wishlist");
        } catch (Exception e) {
            logger.warn("Failed to remove from wishlist for buyer: {}, product: {}", buyerId, productId, e);
            response.sendRedirect(request.getContextPath() + "/buyer/wishlist?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    private void handleMoveToCart(HttpServletRequest request, HttpServletResponse response, int buyerId, int productId)
            throws IOException {
        try {
            wishlistService.moveToCart(buyerId, productId);
            response.sendRedirect(request.getContextPath() + "/buyer/cart?success="
                    + URLEncoder.encode("Item moved to cart", StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.warn("Failed to move item to cart for buyer: {}, product: {}", buyerId, productId, e);
            response.sendRedirect(request.getContextPath() + "/buyer/wishlist?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }
}
