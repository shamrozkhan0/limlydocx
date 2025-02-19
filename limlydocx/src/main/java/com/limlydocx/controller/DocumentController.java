package com.limlydocx.controller;

import com.limlydocx.repository.DocumentRepository;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
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
    @PostMapping("/savecontent/{format}")
    public String saveDocumentContent(
            @PathVariable String format,
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
        StringBuilder uniqueFileName = new StringBuilder("document_" + timestamp + "_" + UUID.randomUUID());


        try {

            if(Objects.equals(format, "pdf")){
                System.out.println("Generating PDF...");
                uniqueFileName.append(".pdf");
                documentService.generatePdfAndUploadToCloud(content, String.valueOf(uniqueFileName));
            } else if (Objects.equals(format, "docx")) {
                System.out.println("Generating DOCX...");
                uniqueFileName.append(".docx");
                documentService.generatesDocxAndUploadToCloud(content , String.valueOf(uniqueFileName));
            } else {
                System.out.println("Invalid format: " + format);

            }


            // Save document info in the database
            documentService.saveDocumentInDatabase(String.valueOf(uniqueFileName), authentication);

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
