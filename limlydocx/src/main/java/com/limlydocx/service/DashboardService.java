package com.limlydocx.service;

import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.entity.User;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import com.limlydocx.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class DashboardService {

    private final GlobalVariable globalVariable;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;



    /**
     * Get user document from the database by using user username
     */
    public void createUserDashboardDocuments(Authentication authentication, String name) {
        String username = globalVariable.getUsername(authentication);

        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User with  username " + username + " not found")
        );

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFileName(name.replaceAll("\\s+", " ").trim());
        documentEntity.setUploadOn(LocalDate.now());
        documentEntity.setUsername(username);
        documentEntity.setUser(user);
        user.getDocuments().add(documentEntity);

        userRepository.save(user);
    }



    /**
     * Gets every document by user's username
     */
    public void getUsersDocumentByUsername(Authentication authentication, Model model){
        String username = globalVariable.getUsername(authentication);
        List<DocumentEntity> dashboards = documentRepository.getAllDocumentByUsername(username);
        model.addAttribute("dashboards", dashboards);
        model.addAttribute("username", username);
    }



}
