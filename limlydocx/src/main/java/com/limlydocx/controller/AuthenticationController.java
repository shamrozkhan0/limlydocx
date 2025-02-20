package com.limlydocx.controller;

import com.limlydocx.entity.User;
import com.limlydocx.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {

    private final AuthenticationService authenticationService;



    /**
     * Send User To Login Page
     * @return Login Page
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", true);
        return "authentication";
    }



    /**
     * Show SignUp Page
     *
     * @param model
     * @return Signup Page
     */
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("signup", true);
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "authentication";
    }



    /**
     * Check User credential & And Saved in the database
     *
     * @param user
     * @param result
     * @param redirectAttributes
     * @param model
     * @return
     */
    @PostMapping("/signup")
    public String registerUserSignup(@Valid @ModelAttribute User user, BindingResult result, RedirectAttributes redirectAttributes, Model model, HttpServletRequest request) {

        log.info("Processing Start for user , {}", user.getEmail());

        try {
            authenticationService.registerUser(user, result, redirectAttributes);
        } catch (Exception ex) {
            log.error("Exception : " + ex);
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/signup";
        }


        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());


        log.info("User is registered");
        return "redirect:/doc";
    }


}
