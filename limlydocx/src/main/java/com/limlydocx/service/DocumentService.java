package com.limlydocx.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import jdk.jfr.Category;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private GlobalVariable globalVariable;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * Saves Data in the database with owner username
     *
     * @param content
     * @param authentication
     */
    public UUID saveDocumentInDatabase(String content, Authentication authentication) {
        String username = globalVariable.getUsername(authentication);
        System.out.println("This is the username " + username);//  for testing

        UUID id = UUID.randomUUID();

        DocumentEntity savedocument = new DocumentEntity();
        savedocument.setContent(content);
        savedocument.setId(id);
        savedocument.setUploadOn(LocalDate.now());
        savedocument.setCreator(username);
        documentRepository.save(savedocument);
        return id;
    }



    public String  downloadDocument(
            String content
    ){

        String uniqueFileName = UUID.randomUUID().toString() + ".pdf"; // Unique filename
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + uniqueFileName); // Save in temp dir

        try (OutputStream outputStream = new FileOutputStream(file)) {
            // Convert HTML content to PDF
            HtmlConverter.convertToPdf(content, outputStream);

            // Upload PDF to Cloudinary
            String cloudinaryUrl = cloudinaryService.uploadFile(file);

            // Delete local temp file
            file.delete();

            return cloudinaryUrl; // Return Cloudinary file URL
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


//    public void downloadDocument(
////            @RequestBody UUID id,
//            String content
//    ) {
//
//        String path = "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\output.pdf";
//        File file = new File(path);
//
//        try {
//
//            OutputStream outputStream = new FileOutputStream(file);
////            String content = documentRepository.getDocumentById(id);
//            HtmlConverter.convertToPdf(content, outputStream);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }







}