package com.limlydocx.controller;

import com.limlydocx.repository.DocumentRepository;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.EditorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Log4j2
public class EditorController {

    private final EditorService documentService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Value("${cloudinary.document.path}")
    private String cloudPath;

    private static final String FILE_NAME_PATTERN = "yyyyMMdd_HHMMSS";



    @GetMapping("/editor/{id}")
    public String sendToEditor(
            @PathVariable("id") UUID id,
            Authentication authentication,
            Model model
    ) {
//        model.addAttribute("editorID", id);
        documentService.checkIfEditorFileExist(id , model);

        log.info("Editor has been found");
        return "editor";
    }



    /**
     * second editor
     */
    @GetMapping("/second")
    public String EditorJS() {
        log.info("User is at the editorJS editor");
        return "editorJS";
    }



    /**
     * Saves the document content, generates a PDF, uploads it to Cloudinary, and stores the document info in the database.
     *
     * @param content            the content to be saved
     * @param authentication     the authentication object
     * @param redirectAttributes attributes for redirect
     * @return the redirect view name
     */
    @PostMapping("/save-content/{format}")
    public String saveDocumentContent(HttpServletRequest httpRequest,
                                      @PathVariable String format,
                                      @RequestParam("content") String content,
                                      @RequestParam("editorID") UUID editorId,
                                      Authentication authentication, RedirectAttributes redirectAttributes) {

        log.info("Editor id {} ", editorId);

        if (authentication == null) {
            return "redirect:/login";
        }

        if (content == null || content.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Content is empty");
            return "redirect:/doc";
        }

        log.info("okay 1");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(FILE_NAME_PATTERN));
        StringBuilder uniqueFileName = new StringBuilder("document_" + timestamp + "_" + UUID.randomUUID());

        // This ensure pdf is create with success status
        ResponseEntity<String> status = null;

        try {
            System.out.println("okay 1");
            status = this.checkDocumentFormat(format, uniqueFileName, content, status);
            System.out.println("okay 2");
            this.checklIfDocumentCreatedAndReturn(status, redirectAttributes, uniqueFileName.toString(), authentication, content, editorId);
            System.out.println("okay 3");

        } catch (Exception e) {

            log.error("Error processing document: {} of format ({})", e.getMessage(), format);
            redirectAttributes.addFlashAttribute("error", status.getBody());

        }
        return "redirect:/editor/" + editorId;
    }



    /**
     * Checks the format for the document, appends the extension, and processes it accordingly.
     *
     * @param format         The format of the document (e.g., pdf, docx)
     * @param uniqueFileName The generated unique file name
     * @param content        The content of the document
     * @param status         ResponseEntity holding the document creation status
     * @return ResponseEntity Indicating the status of document processing
     */
    public ResponseEntity<String> checkDocumentFormat(String format, StringBuilder uniqueFileName, String content, ResponseEntity<String> status) {

        switch (format.toLowerCase()) {

            case "pdf" -> {
                uniqueFileName.append(".pdf");
                status = documentService.generatePdfAndUploadOnCloud(content, uniqueFileName.toString());
                log.info("USER SELECT PDF");
            }

            case "docx" -> {
                uniqueFileName.append(".docx");
                status = documentService.generateDocxFile(content, uniqueFileName.toString());
                log.info("USER SELECT DOCX");
            }

            default -> log.error("Invalid format {}", format);
        }

        return status;
    }



    /**
     * It will check if document is created and return to user to download
     *
     * @param status
     * @param redirectAttributes
     * @param uniqueFileName
     * @param authentication
     * @param content
     */
    public void checklIfDocumentCreatedAndReturn(ResponseEntity<String> status, RedirectAttributes redirectAttributes,
                                                 String uniqueFileName, Authentication authentication, String content,
                                                 UUID editorId) {


        if (status != null && status.getStatusCode().is2xxSuccessful()) {
            log.info("Status code {}", status.getStatusCode());

            // Save document info in the database
            documentService.saveDocumentInDatabase(String.valueOf(uniqueFileName), authentication, editorId, content);
            // Provide download URL to the user
//            redirectAttributes.addFlashAttribute("download_url", cloudPath + uniqueFileName);

            // Preserve content in the editor
            redirectAttributes.addFlashAttribute("content", content);
            redirectAttributes.addFlashAttribute("success", status.getBody());

        } else if (status == null) {
            log.info("task is empty" + status);
        } else {
            System.out.println("status error");
            // Preserve content in the content in editor
            redirectAttributes.addFlashAttribute("content", content);
            redirectAttributes.addFlashAttribute("error", "Error creating document");
            throw new RuntimeException();
        }
    }


}
