package com.sameer.sameermart.cart;

import com.sameer.sameermart.product.ProductListServlet;
import com.sameer.sameermart.user.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartServlet {
    private final CartService cartService;

    public CartServlet(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String view(HttpSession session, Model model) {
        SessionUser user = ProductListServlet.requireBuyer(session);
        model.addAttribute("cart", cartService.cart(user.id()));
        return "cart";
    }
}
