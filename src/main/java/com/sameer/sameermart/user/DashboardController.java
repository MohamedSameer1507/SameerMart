package com.sameer.sameermart.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public String dashboard(HttpSession session) {
        return switch (currentUser(session).role()) {
            case BUYER -> "redirect:/dashboard/buyer";
            case SELLER -> "redirect:/dashboard/seller";
            case ADMIN -> "redirect:/dashboard/admin";
        };
    }

    @GetMapping("/buyer")
    public String buyerDashboard(HttpSession session, Model model) {
        model.addAttribute("user", requireRole(session, UserRole.BUYER));
        return "buyer-dashboard";
    }

    @GetMapping("/seller")
    public String sellerDashboard(HttpSession session, Model model) {
        model.addAttribute("user", requireRole(session, UserRole.SELLER));
        return "seller-dashboard";
    }

    @GetMapping("/admin")
    public String adminDashboard(HttpSession session, Model model) {
        model.addAttribute("user", requireRole(session, UserRole.ADMIN));
        return "admin-dashboard";
    }

    private SessionUser requireRole(HttpSession session, UserRole role) {
        SessionUser user = currentUser(session);
        if (user.role() != role) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot access this dashboard.");
        }
        return user;
    }

    private SessionUser currentUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SessionUser.ATTRIBUTE);
    }
}
