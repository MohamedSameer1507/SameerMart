package com.sameer.sameermart.cart;

import com.sameer.sameermart.product.ProductListServlet;
import com.sameer.sameermart.user.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart/remove")
public class RemoveCartServlet {
    private final CartService cartService;
    public RemoveCartServlet(CartService cartService) { this.cartService = cartService; }
    @PostMapping
    public String remove(@RequestParam long productId, HttpSession session) {
        SessionUser user = ProductListServlet.requireBuyer(session);
        cartService.remove(user.id(), productId);
        return "redirect:/cart";
    }
}
