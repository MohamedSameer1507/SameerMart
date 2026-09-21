package com.sameer.sameermart.cart;

import com.sameer.sameermart.product.ProductListServlet;
import com.sameer.sameermart.user.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart/add")
public class AddToCartServlet {
    private final CartService cartService;

    public AddToCartServlet(CartService cartService) { this.cartService = cartService; }

    @PostMapping
    public String add(@RequestParam long productId, @RequestParam(defaultValue = "1") int quantity, HttpSession session) {
        SessionUser user = ProductListServlet.requireBuyer(session);
        cartService.add(user.id(), productId, quantity);
        return "redirect:/cart";
    }
}
