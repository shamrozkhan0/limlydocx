package com.limlydocx.controller;

import com.limlydocx.repository.DocumentRepository;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.DocumentService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Log4j2
public class DocumentController {

    private final DocumentService documentService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;


    @Value("${cloudinary.document.path}")
    private String cloudPath;

    /**
     * Directs the user to the editor page.
     *
     * @return the editor view name
     */
    @GetMapping("/doc")
    public String showEditor() {
        log.info("User is at the Editor page");
        return "editor";
    }




    /**
     * Saves the document content, generates a PDF, uploads it to Cloudinary, and stores the document info in the database.
     *
     * @param content            the content to be saved
     * @param authentication     the authentication object
     * @param redirectAttributes attributes for redirect
     * @return the redirect view name
     */
    @PostMapping("/savecontent")
    public String saveDocumentContent(
            @RequestParam("content") String content,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (authentication == null) {
            return "redirect:/login";
        }

        if (content == null || content.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Content is empty");
            return "redirect:/doc";
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFileName = "document_" + timestamp + "_" + UUID.randomUUID().toString() + ".pdf";

        try {
            // Upload document to Cloudinary
            documentService.generatePdfAndUploadToCloud(content, uniqueFileName);

            // Save document info in the database
            documentService.saveDocumentInDatabase(uniqueFileName, authentication);

            // Provide download URL to the user
            redirectAttributes.addFlashAttribute("download_url", cloudPath + uniqueFileName);

            // Preserve content in the editor
            redirectAttributes.addFlashAttribute("content", content);

        } catch (Exception e) {
            log.error("Error processing document: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing the document");
        }

        return "redirect:/doc";
    }
}
