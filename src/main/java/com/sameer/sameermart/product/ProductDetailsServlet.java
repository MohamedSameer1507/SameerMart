package com.sameer.sameermart.product;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/** Spring MVC controller serving the ProductDetailsServlet responsibility. */
@Controller
@RequestMapping("/products")
public class ProductDetailsServlet {
    private final ProductService productService;

    public ProductDetailsServlet(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public String details(@PathVariable long id, Model model) {
        model.addAttribute("product", productService.product(id));
        return "product-details";
    }
}
