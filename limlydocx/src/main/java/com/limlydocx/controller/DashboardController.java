package com.limlydocx.controller;

import com.limlydocx.entity.Dashboard;
import com.limlydocx.repository.DashboardRepository;
import com.limlydocx.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardRepository dashboardRepository;



    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model){
        String username = authentication.getName();
        List<Dashboard> dashboards = dashboardRepository.getAllDocumentByOwner(username);
        System.out.println(dashboards.get(0).getOwner());
        model.addAttribute("dashboards" , dashboards);
        return "dashboard";
    }



    @PostMapping("/dashboard/generateEditor")
    public String generateUserEditor(
            Authentication authentication,
            @RequestParam("dashboard-name") String name
    ){
        dashboardService.createUserDashboard(authentication, name);
        return "redirect:/dashboard";
    }










}

