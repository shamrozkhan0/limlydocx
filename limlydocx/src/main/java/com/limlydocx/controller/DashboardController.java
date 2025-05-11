package com.limlydocx.controller;

import com.limlydocx.repository.DocumentRepository;
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
    private final DocumentRepository documentRepository;



    // FOR TESTING REMOVED, WHEN TESTING COMPLETES
    @GetMapping("/bla")
    public String wqel(){
        return "editor";
    }



    /**
     * This will find the document of user's username and show them in the dashboard
     *
     * @param authentication get the user authentication to get document from database
     * @param model sends document from database to user
     * @return redirect to dashboard page
     */
    @GetMapping("/dashboard")
    public String loadUserDashboardWithDocument(Authentication authentication, Model model) {
        try {
          dashboardService.getUsersDocumentByUsername(authentication, model);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "dashboard";
    }



    /**
     * This will generate the document for the user
     *
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
     *
     * @param id Gets id to delete the document
     * @return redirect to dashboard page
     */
    @DeleteMapping("delete/document/{id}")
    @Transactional
    public String deleteDocument(@PathVariable("id") UUID id) {
        try {
            documentRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/dashboard";
        }
        return "redirect:/dashboard";
    }




}