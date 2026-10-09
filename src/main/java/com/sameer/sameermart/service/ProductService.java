package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.model.Product;

import java.util.List;

public interface ProductService {
    Product getProductById(int id);
    List<Product> getAllActiveProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> searchProducts(String keyword);
    List<Product> getSellerProducts(int sellerId);
    boolean createProduct(Product product);
    boolean updateProduct(Product product);
    ProductDAO.DeletionResult deleteProduct(int id, int sellerId);
}
