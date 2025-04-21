package com.limlydocx.controller;

import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.entity.User;
import com.limlydocx.repository.EditorRepository;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.DashboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
@Slf4j
public class DashboardController {

    private final UserRepository userRepository;
    private final DashboardService dashboardService;
//    private final DashboardRepository dashboardRepository;
    private final EditorRepository documentRepository;


    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model){
        try {
            String username = authentication.getName();
            Optional<User> users = userRepository.findUserByEmail(username);
            model.addAttribute("user", users.get());

//            List<DashboardDocumentEntity> dashboards = dashboardRepository.getAllDocumentByOwner(users.get().getUsername());
            List<DocumentEntity> dashboards =  documentRepository.getAllDocumentByCreator(users.get().getUsername());
            log.info("this is the repo of {} ", dashboards);
            model.addAttribute("dashboards" , dashboards);

        } catch (Exception e){
            e.printStackTrace();
        }
        return "dashboard";
    }



    @GetMapping("/create")
    public String showCreatePage(){
        return "dash";
    }



    @PostMapping("/dashboard/generateEditor")
    public String generateUserEditor(
            Authentication authentication,
            @RequestParam("dashboard-name") String name
    ){
        dashboardService.createUserDashboardDocuments(authentication, name);
        return "redirect:/dashboard";
    }



    @DeleteMapping("delete/document/{id}")
    @Transactional
    public String deleteDocument(@PathVariable("id") UUID id) {
        try {
            documentRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception to understand any errors
            return "redirect:/dashboard";  // Redirect to dashboard in case of error
        }
        return "redirect:/dashboard";  // Redirect to dashboard after successful deletion
    }


    /*
    - setup file path and name for cloudinary
    -
     */




}