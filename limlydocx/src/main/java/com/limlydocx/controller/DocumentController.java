package com.limlydocx.controller;

import com.limlydocx.repository.DocumentRepository;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentService documentService;



    @RequestMapping(path = "/doc" , method = {RequestMethod.GET})
    public String showEditor(){
        return "editor";
    }



    // for testing3
    @RequestMapping(path = "/savecontent" , method = RequestMethod.POST)
    public String saveDocumentContent(@RequestBody String content,
                                      Authentication userAuthentication,
                                      RedirectAttributes redirectAttributes){
        try{
            System.out.println("content reach : " + content );
            documentService.saveDocumentInDatabase(content , userAuthentication);
            documentService.generatePdf(content, redirectAttributes);

        } catch (Exception ex){
            System.out.println(ex.getMessage());
            return "redirect:/doc";
        }

        return "redirect:/doc";
    }







}
