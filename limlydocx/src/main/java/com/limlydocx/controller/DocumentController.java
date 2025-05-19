package com.limlydocx.controller;

import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.repository.DocumentRepository;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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



    @GetMapping("/editor/{id}")
    public String showEditor(@PathVariable("id") UUID id, Model model) {

       ResponseEntity<DocumentEntity> response =  documentService.checkIfEditorFileExist(id,model);

       if(!response.getStatusCode().is2xxSuccessful()){
           return"redirect:/dashboard";
       }

        model.addAttribute("document", response.getBody());
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
     * @param format  PDF / DOCX
     * @param content Inline Styling Content to create pdf/docx
     * @param contentDB Minified version which to be saved in database
     * @param editorId to authenticate if it's present
     * @param authentication
     * @param redirectAttributes
     *
     * @throws InterruptedException
     */
    @PostMapping("/save-content/{format}")
    public String saveDocumentContent(
                                      @PathVariable String format,
                                      @RequestParam("content") String content,
                                      @RequestParam("contentDB") String contentDB,
                                      @RequestParam("editorID") UUID editorId,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) throws InterruptedException
    {
        if (authentication == null) {
            return "redirect:/login";
        }

        if (content == null  || content.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Content is empty");
            return "redirect:/editor/" + editorId;
        }
        log.info("Content for pdf/docx {}", content);

        if(contentDB == null || contentDB.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Write something in editor");
            return "redirect:/editor/"+ editorId;
        }


        // This ensure pdf is create with success status
        ResponseEntity<String> status = null;

        try {

            String uniqueFileName = documentService.generateUniqueFileName();
            status = documentService.checkDocumentFormat(format, uniqueFileName, content, status);
            documentService.checklIfDocumentCreatedAndReturn(status, redirectAttributes, uniqueFileName, authentication, contentDB, editorId);

        } catch (Exception e) {

            log.error("Error processing document: {} of format ({})", e.getMessage(), format);
            redirectAttributes.addFlashAttribute("error", status.getBody());

        }
        return "redirect:/editor/" + editorId;
    }




}
