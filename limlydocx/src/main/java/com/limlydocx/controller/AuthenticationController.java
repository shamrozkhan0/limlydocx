package com.limlydocx.controller;

import com.limlydocx.dto.LoginDto;
import com.limlydocx.dto.SignupDto;
import com.limlydocx.entity.User;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage( @RequestParam(required = false) String error , Model model){
        System.out.println("login page");
        return "login";
    }


    @GetMapping("/signup")
    public String showSignupPage(Model model){
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute User user, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        System.out.println("post mapping signup");


        try {
            authenticationService.registerUser(user, result, redirectAttributes);
        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute("user", user);
//            model.addAttribute("or/g.springframework.validation.BindingResult.user" , user);
//            model.addAttribute("error",ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            System.out.println(ex.getMessage());
            return "redirect:/signup";
        }

        System.out.println("done");
        return "redirect:/hello";
    }




}
