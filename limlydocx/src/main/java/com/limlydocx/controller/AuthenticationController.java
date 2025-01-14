package com.limlydocx.controller;

import com.limlydocx.entity.User;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.AuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);


    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "signup";
    }


    @PostMapping("/signup")
    public String registerUserSignup(@Valid @ModelAttribute User user, BindingResult result, RedirectAttributes redirectAttributes, Model model) {

        log.info("Processing Start for user : {}", user.getEmail());

        try {
            authenticationService.registerUser(user, result, redirectAttributes);

        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/signup";
        }

        log.info("User is registered");
        return "redirect:/hello";
    }


}
