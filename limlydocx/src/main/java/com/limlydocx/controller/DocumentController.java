package com.limlydocx.controller;

import com.limlydocx.repository.DocumentRepository;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
            @RequestBody String content,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // if user not and somehow access the editor he returns to login page
            if (authentication == null) {
                return "/login";
            }

            String decodedContent= URLDecoder.decode(content, StandardCharsets.UTF_8.name());

            // for debug
            System.out.println("content: " + decodedContent);

            // returns a UUID
            UUID id = documentService.saveDocumentInDatabase(content, authentication);

            // redirect the id which activates the button which help to download
            redirectAttributes.addFlashAttribute("btn", id);
            redirectAttributes.addFlashAttribute("content", decodedContent);

        } catch (Exception e) {
            e.getMessage();
            return "redirect:/doc";
        }
        // returns to the editor with the button with pdf id which saves in the database
        return "redirect:/doc";
    }



    @RequestMapping(path = "/download-document", method = RequestMethod.POST)
    public String DownloadDocument(@RequestParam UUID id) {
        System.out.println("id : " + id);
        System.out.println("bweuoqow");
        return "redirect:/doc";
    }



    /**
     *  TODO AT HOME
     *  CLEAN HTML CODE USING THYMELEAF FRAGMENTS
     *  AND SOME HTTP CODE
     */


}
