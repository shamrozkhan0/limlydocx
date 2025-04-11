package com.limlydocx.controller;

import com.limlydocx.entity.Dashboard;
import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.entity.User;
import com.limlydocx.repository.DashboardRepository;
import com.limlydocx.repository.DocumentRepository;
import com.limlydocx.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final DashboardRepository dashboardRepository;


    @GetMapping("/start-at")
    public String sayhello(){
        return "hello world  31-December-2024";
    }



    @GetMapping("/users")
    public List<User> showAllusers(){
        return userRepository.findAll();
    }



    @GetMapping("/docs")
    public List<DocumentEntity> showSavedDocument(){
        return documentRepository.findAll();
    }



    @GetMapping("/dash")
    public List<Dashboard> showDashboard(){
        return dashboardRepository.findAll();
    }




}
