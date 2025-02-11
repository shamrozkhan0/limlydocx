package com.limlydocx.controller;

import com.limlydocx.repository.DocumentRepository;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentService documentService;



    /**
     * Sends user to editor
     * @return editor.html
     */
    @RequestMapping(path = "/doc", method = {RequestMethod.GET})
    public String showEditor() {
        System.out.println("lala la a");
        return "editor";
    }



    @RequestMapping(path = "/savecontent", method = RequestMethod.POST)
    public String saveDocumentContent(
            @RequestParam("content") String content,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Check If user is Authenticated
            if (authentication == null) {
                return "/login";
            }

            // Check if Content is null or is empty
            if(content == null ||  content.isEmpty()){
                redirectAttributes.addFlashAttribute("error" , "Content is empty");
                return "redirect:/doc";
            }

            // for testing
            System.out.println(content);

            // Method return a unique id which will be saves in id
            UUID id = documentService.saveDocumentInDatabase(content, authentication);

            // Currently testing
            documentService.downloadDocument(content);


            // Redirect id for user to download with it
            redirectAttributes.addFlashAttribute("btn", id);

            // Preserved Content in the editor
            redirectAttributes.addFlashAttribute("content", content);


            return "redirect:/doc";

        } catch (Exception e) {
//            System.out.println(e.getMessage());
            e.printStackTrace();
            return "redirect:/doc";
        }
    }







}
