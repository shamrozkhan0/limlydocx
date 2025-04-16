package com.limlydocx.controller;

import com.limlydocx.entity.DashboardDocumentEntity;
import com.limlydocx.entity.User;
import com.limlydocx.repository.DashboardRepository;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.DashboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@Slf4j
public class DashboardController {

    private final UserRepository userRepository;
    private final DashboardService dashboardService;
    private final DashboardRepository dashboardRepository;



    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model){
        try {
            String username = authentication.getName();

            Optional<User> users = userRepository.findUserByEmail(username);
            model.addAttribute("user", users.get());

            List<DashboardDocumentEntity> dashboards = dashboardRepository.getAllDocumentByOwner(username);
//            log.info("this is the repo of {} : {}" ,);
            model.addAttribute("dashboards" , dashboards);

        } catch (Exception e){
            e.printStackTrace();
        }
        return "dashboard";
    }



    @PostMapping("/dashboard/generateEditor")
    public String generateUserEditor(
            Authentication authentication,
            @RequestParam("dashboard-name") String name
    ){
        dashboardService.createUserDashboardDocuments(authentication, name);
        return "redirect:/dashboard";
    }










}

