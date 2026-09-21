package com.sameer.sameermart.cart;

import com.sameer.sameermart.product.ProductListServlet;
import com.sameer.sameermart.user.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart/update")
public class UpdateCartServlet {
    private final CartService cartService;
    public UpdateCartServlet(CartService cartService) { this.cartService = cartService; }
    @PostMapping
    public String update(@RequestParam long productId, @RequestParam int quantity, HttpSession session) {
        SessionUser user = ProductListServlet.requireBuyer(session);
        cartService.update(user.id(), productId, quantity);
        return "redirect:/cart";
    }
}
