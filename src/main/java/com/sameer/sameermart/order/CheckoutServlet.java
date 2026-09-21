package com.sameer.sameermart.order;

import com.sameer.sameermart.product.ProductListServlet;
import com.sameer.sameermart.user.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/** Spring MVC controller serving the checkout and mock payment servlet responsibilities. */
@Controller
@RequestMapping("/checkout")
public class CheckoutServlet {
    private final CheckoutService checkoutService;

    public CheckoutServlet(CheckoutService checkoutService) { this.checkoutService = checkoutService; }

    @GetMapping
    public String checkout(HttpSession session, Model model) {
        SessionUser user = ProductListServlet.requireBuyer(session);
        model.addAttribute("cart", checkoutService.checkoutCart(user.id()));
        return "checkout";
    }

    @PostMapping("/payment")
    public String payment(HttpSession session, Model model) {
        SessionUser user = ProductListServlet.requireBuyer(session);
        model.addAttribute("cart", checkoutService.checkoutCart(user.id()));
        return "payment";
    }

    @PostMapping("/confirm")
    public String confirm(HttpSession session) {
        SessionUser user = ProductListServlet.requireBuyer(session);
        long orderId = checkoutService.confirmMockPayment(user.id());
        return "redirect:/checkout/success?orderId=" + orderId;
    }

    @GetMapping("/success")
    public String success(@RequestParam long orderId, HttpSession session, Model model) {
        SessionUser user = ProductListServlet.requireBuyer(session);
        if (!checkoutService.orderBelongsTo(orderId, user.id())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found.");
        }
        model.addAttribute("orderId", orderId);
        return "order-success";
    }
}
