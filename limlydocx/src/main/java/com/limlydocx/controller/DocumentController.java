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

            if (authentication == null) {
                return "/login";
            }

            if(!content.isEmpty() ||  content != null){
                redirectAttributes.addFlashAttribute("error" , "Content is empty");
                return "redirect:/doc";
            }

            System.out.println(content);

            UUID id = documentService.saveDocumentInDatabase(content, authentication);
            documentService.downloadDocument(content);

            redirectAttributes.addFlashAttribute("btn", id);
            redirectAttributes.addFlashAttribute("content", content);

            return "redirect:/doc";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/doc";
        }
    }







}
