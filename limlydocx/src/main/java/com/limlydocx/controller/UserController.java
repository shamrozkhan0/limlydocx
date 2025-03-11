package com.limlydocx.controller;

import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.entity.User;
import com.limlydocx.repository.DocumentRepository;
import com.limlydocx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;



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





}
