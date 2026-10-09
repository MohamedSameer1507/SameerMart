package com.sameer.sameermart.servlet.api;

import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.dto.ApiResponse;
import com.sameer.sameermart.model.Product;
import com.sameer.sameermart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * REST API endpoint: GET /api/v1/products
 * Supports:
 * - ?id=123 (single product lookup)
 * - ?category=Groceries (category filter)
 * - ?search=honey (keyword search)
 * - default: all active products
 */
@WebServlet(name = "ProductApiServlet", urlPatterns = {"/api/v1/products"})
public class ProductApiServlet extends HttpServlet {

    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productDAO = new ProductDAO();
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam.trim());
                Product product = productDAO.findById(id);
                if (product == null || !product.isActive()) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write(JsonUtil.toJson(ApiResponse.error("NOT_FOUND", "Product not found")));
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(JsonUtil.toJson(ApiResponse.ok(product)));
                return;
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(JsonUtil.toJson(ApiResponse.error("INVALID_ID", "Product ID must be numeric")));
                return;
            }
        }

        String category = request.getParameter("category");
        String search = request.getParameter("search");

        List<Product> products;
        if (category != null && !category.trim().isEmpty()) {
            products = productDAO.findByCategory(category.trim());
        } else if (search != null && !search.trim().isEmpty()) {
            products = productDAO.searchProducts(search.trim());
        } else {
            products = productDAO.findAllActive();
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtil.toJson(ApiResponse.ok(products)));
    }
}
