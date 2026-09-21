package com.sameer.sameermart.user;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "register";
    }

    @PostMapping
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            form.setPassword(null);
            return "register";
        }

        try {
            userService.register(form);
        } catch (DuplicateEmailException exception) {
            bindingResult.rejectValue("email", "duplicate", exception.getMessage());
            return "register";
        } catch (InvalidRegistrationRoleException exception) {
            bindingResult.rejectValue("role", "invalid", exception.getMessage());
            return "register";
        }

        return "redirect:/login?registered";
    }
}
