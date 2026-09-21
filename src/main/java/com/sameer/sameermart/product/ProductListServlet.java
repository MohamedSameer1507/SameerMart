package com.sameer.sameermart.product;

import com.sameer.sameermart.user.SessionUser;
import com.sameer.sameermart.user.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/** Spring MVC controller serving the ProductListServlet responsibility. */
@Controller
@RequestMapping("/products")
public class ProductListServlet {
    private final ProductService productService;

    public ProductListServlet(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category,
                       Model model) {
        model.addAttribute("products", productService.browse(keyword, category));
        model.addAttribute("categories", productService.categories());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("selectedCategory", category == null ? "" : category);
        return "product-list";
    }

    public static SessionUser requireBuyer(HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(SessionUser.ATTRIBUTE);
        if (user == null || user.role() != UserRole.BUYER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Buyer access is required.");
        }
        return user;
    }
}
