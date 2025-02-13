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


    /**
     * Send User To Login Page
     *
     * @return Login Page
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login" , true);
        model.addAttribute("signup" , false);
        return "authentication";
    }


    /**
     * Show SignUp Page
     * @param model
     * @return Signup Page
     */
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("signup" , true);
        model.addAttribute("login" , false);
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "authentication";
    }



    // Incomplete Its has many errors
    @PostMapping("/signup")
    public String registerUserSignup(@Valid @ModelAttribute User user, BindingResult result, RedirectAttributes redirectAttributes, Model model) {

        log.info("Processing Start for user : {}", user.getEmail());

        try {
            authenticationService.registerUser(user, result, redirectAttributes);
        } catch (Exception ex) {
            System.out.println("exception:" + ex);

            // Rediret To The SignUp Page With User Credential When SignUp Fail
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/signup";
        }

        log.info("User is registered");
        return "redirect:/hello";
    }


}
