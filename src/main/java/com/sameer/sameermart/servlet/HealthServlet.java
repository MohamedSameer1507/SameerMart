package com.sameer.sameermart.servlet;

import com.google.gson.JsonObject;
import com.sameer.sameermart.config.DatabaseConfig;
import com.sameer.sameermart.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Health check endpoint under GET /api/v1/health.
 * Validates web runtime and database connectivity via connection pool SELECT 1.
 */
@WebServlet(name = "HealthServlet", urlPatterns = {"/api/v1/health"})
public class HealthServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(HealthServlet.class);

    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        this.dataSource = DatabaseConfig.getDataSource();
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        boolean dbUp = checkDatabaseHealth();

        JsonObject healthJson = new JsonObject();
        if (dbUp) {
            response.setStatus(HttpServletResponse.SC_OK);
            healthJson.addProperty("status", "UP");
            healthJson.addProperty("db", "UP");
        } else {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            healthJson.addProperty("status", "DOWN");
            healthJson.addProperty("db", "DOWN");
            logger.error("Health check failed: Database connection down");
        }

        response.getWriter().write(JsonUtil.toJson(healthJson));
        response.getWriter().flush();
    }

    private boolean checkDatabaseHealth() {
        if (dataSource == null) {
            return false;
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet rs = statement.executeQuery()) {
            return rs.next();
        } catch (Exception e) {
            logger.warn("Database health check probe failed: {}", e.getMessage());
            return false;
        }
    }
}
