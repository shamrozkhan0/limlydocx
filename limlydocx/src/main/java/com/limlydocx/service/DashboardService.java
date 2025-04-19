package com.limlydocx.service;

import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
@Log4j2
public class DashboardService {

//    private final DashboardRepository dashboardRepository;
    private final GlobalVariable globalVariable;
    private final DocumentRepository documentRepository;



    /**
     *  Get user document from the database by using user username
     *
     * @param authentication
     * @param name
     */
    public void createUserDashboardDocuments(Authentication authentication, String name) {

        String username = globalVariable.getUsername(authentication);

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFileName(name.replaceAll("\\s+"," ").trim());
        documentEntity.setUploadOn(LocalDate.now());
        documentEntity.setCreator(username);


//        DashboardDocumentEntity dashboard = new DashboardDocumentEntity();
//        dashboard.setOwner(username);
//        dashboard.setCreatedOn(LocalDate.now());
//        dashboard.setName(name);
//        log.info("Dashboard credential {} , {}", username, name);

        try {
//            dashboardRepository.save(dashboard);
            documentRepository.save(documentEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }




}
