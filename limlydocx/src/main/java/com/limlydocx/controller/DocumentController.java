package com.limlydocx.controller;

import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;


    @GetMapping("/doc")
    public String showEditor(){
        return "editor";
    }


    @PostMapping("/savecontent")
    public String saveDocumentContent(@RequestBody String content){
        try{
            System.out.println(content);
            DocumentEntity docEntity = new DocumentEntity();
            docEntity.setContent(content);
            documentRepository.save(docEntity);
            System.out.println("user is saved");

        } catch (Exception ex){
            System.out.println(ex.getMessage());
            return "redirect:/editor";
        }


        return "redirect:/docs";

    }

}
