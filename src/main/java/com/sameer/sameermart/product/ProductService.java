package com.sameer.sameermart.product;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> browse(String keyword, String category) {
        return productDAO.findAvailable(keyword, category);
    }

    public List<String> categories() {
        return productDAO.findCategories();
    }

    public Product product(long id) {
        return productDAO.findAvailableById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found."));
    }
}
