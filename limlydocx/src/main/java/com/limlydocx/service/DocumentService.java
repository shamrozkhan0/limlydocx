package com.limlydocx.service;

import com.cloudinary.Cloudinary;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Log4j2
public class DocumentService {

    private final Cloudinary cloudinary;
    private final GlobalVariable globalVariable;
    private final DocumentRepository documentRepository;



    /**
     * Saves document information in the database.
     * @param uniqueFileName the unique file name
     * @param authentication the authentication object
     */
    public void saveDocumentInDatabase(String uniqueFileName, Authentication authentication) {
        String username = globalVariable.getUsername(authentication);
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFileName(uniqueFileName);
        documentEntity.setUploadOn(LocalDate.now());
        documentEntity.setCreator(username);
        try {
            documentRepository.save(documentEntity);
        } catch (Exception e) {
            log.error("Error saving document to database: {}", e.getMessage());
            throw new RuntimeException("Database save failed", e);
        }
    }



    /**
     * Generates a PDF from HTML content and uploads it to Cloudinary.
     * @param content the HTML content
     * @param uniqueFileName the unique file name
     */
    public void generatePdfAndUploadToCloud(String content, String uniqueFileName) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(content, byteArrayOutputStream, properties);
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();
            uploadToCloudinary(uniqueFileName, pdfBytes);
        } catch (IOException e) {
            log.error("Error generating PDF: {}", e.getMessage());
            throw new RuntimeException("PDF generation failed", e);
        }
    }



    /**
     * Uploads the PDF byte array to Cloudinary.
     * @param uniqueFileName the unique file name
     * @param pdfBytes the PDF byte array
     */
    private void uploadToCloudinary(String uniqueFileName, byte[] pdfBytes) {
        Map<String, Object> options = new HashMap<>();
        options.put("resource_type", "raw");
        options.put("public_id", uniqueFileName);
        options.put("overwrite", true);
        options.put("tags", "pdf,document");

        try {
            cloudinary.uploader().upload(pdfBytes, options);
        } catch (IOException e) {
            log.error("Error uploading to Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }
}
