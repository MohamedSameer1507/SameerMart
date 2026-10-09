package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.exception.ResourceNotFoundException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;

    public ProductServiceImpl() {
        this.productDAO = new ProductDAO();
    }

    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public Product getProductById(int id) {
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return productDAO.findAllActive();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllActiveProducts();
        }
        return productDAO.findByCategory(category.trim());
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveProducts();
        }
        return productDAO.searchProducts(keyword.trim());
    }

    @Override
    public List<Product> getSellerProducts(int sellerId) {
        return productDAO.findBySellerId(sellerId);
    }

    @Override
    public boolean createProduct(Product product) {
        validateProduct(product);
        return productDAO.addProduct(product);
    }

    @Override
    public boolean updateProduct(Product product) {
        if (product == null || product.getId() <= 0) {
            throw new ValidationException("Product id must be specified for update");
        }
        validateProduct(product);
        return productDAO.updateProduct(product);
    }

    @Override
    public ProductDAO.DeletionResult deleteProduct(int id, int sellerId) {
        if (id <= 0 || sellerId <= 0) {
            throw new ValidationException("Invalid product or seller ID");
        }
        return productDAO.deleteProduct(id, sellerId);
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new ValidationException("Product cannot be null");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new ValidationException("Product category is required");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Product price must be greater than zero");
        }
        if (product.getQuantity() < 0) {
            throw new ValidationException("Product quantity cannot be negative");
        }
    }
}
