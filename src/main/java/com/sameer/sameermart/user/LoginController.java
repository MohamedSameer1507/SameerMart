package com.sameer.sameermart.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showForm(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @PostMapping
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            form.setPassword(null);
            return "login";
        }

        try {
            SessionUser user = userService.login(form);

            // Session-fixation protection: discard any anonymous/pre-login session.
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession freshSession = request.getSession(true);
            freshSession.setAttribute(SessionUser.ATTRIBUTE, user);
            return "redirect:/dashboard";
        } catch (InvalidCredentialsException exception) {
            form.setPassword(null);
            bindingResult.reject("invalidCredentials", exception.getMessage());
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout";
    }
}
