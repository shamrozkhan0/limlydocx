package com.limlydocx.controller;

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

import java.util.UUID;

@Controller
@AllArgsConstructor
@Slf4j
public class DashboardController {

    private final UserRepository userRepository;
    private final DashboardService dashboardService;
    private final EditorRepository documentRepository;



    /**
     * This will find the document of user's username and show then in the dashboard
     *
     * @param authentication
     * @param model
     * @return redirect to dashboard page
     */
    @GetMapping("/dashboard")
    public String loadUserDashboardWithDocument(Authentication authentication, Model model) {
        try {
          dashboardService.getUsersDocumentByUsername(authentication, model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "dashboard";
    }



    /**
     * This will generates the document for the user
     * @param authentication uses for user authentication
     * @param name FileName for processing and naming
     * @return redirect to dashboard page
     */
    @PostMapping("/dashboard/generateDocument")
    public String generateUserEditor(
            Authentication authentication,
            @RequestParam("dashboard-name") String name
    ) {
        try{
            dashboardService.createUserDashboardDocuments(authentication, name);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/dashboard";
    }



    /**
     * This will delete the document of user if its exists
     * @param id
     * @return redirect to dashboard page
     */
    @DeleteMapping("delete/document/{id}")
    @Transactional
    public String deleteDocument(@PathVariable("id") UUID id) {
        try {
            documentRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/dashboard";
        }
        return "redirect:/dashboard";
    }




}