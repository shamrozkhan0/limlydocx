package com.limlydocx.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private GlobalVariable globalVariable;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private Cloudinary cloudinary;


    public DocumentService(
            @Value("${cloud.name}") String cloudName,
            @Value("${api.key}") String apiKey,
            @Value("${api.secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

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


    public String  uploadDocumentToCloudinary(String content) {
        ByteArrayOutputStream byteArrayOutputStream = null;

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFileName = "pdf_" + timestamp + "_" + UUID.randomUUID().toString() + ".pdf";

//        String path = "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\"+ uniqueFileName +".pdf";
//        File file = new File(path);

        try {
//            OutputStream outputStream = new FileOutputStream(file);

            // Create an in-memory ByteArrayOutputStream
            byteArrayOutputStream = new ByteArrayOutputStream();


            // Convert HTML content to PDF with custom options for better quality
            ConverterProperties properties = new ConverterProperties();

            HtmlConverter.convertToPdf(content,byteArrayOutputStream, properties);

            // You can add custom PDF properties here if needed
//            HtmlConverter.convertToPdf(content, byteArrayOutputStream, properties);

            // Convert ByteArrayOutputStream to byte[]
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            // Validate PDF size before upload (optional)
            if (pdfBytes.length == 0) {
                throw new IllegalArgumentException("Generated PDF is empty");
            }

            // Upload options
            Map<String, Object> options = new HashMap<>();
            options.put("resource_type", "raw");
            options.put("public_id", uniqueFileName);
            options.put("overwrite", true);
//            options.put("folder", "pd3fs"); // Organize files in a folder
            options.put("tags", "pdf,document"); // Add tags for better organization

            // Upload PDF to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    pdfBytes,
                    options
            );
            // Inside uploadDocumentToCloudinary method after getting secure_url
            String cloudinaryUrl = (String) uploadResult.get("secure_url");

            if (cloudinaryUrl == null) {
                throw new RuntimeException("Failed to get secure URL from upload result");
            }

            if (cloudinaryUrl != null) {
                // Insert the 'fl_attachment' transformation into the URL
                cloudinaryUrl = cloudinaryUrl.replace("/upload/", "/upload/fl_attachment/");
            }

            System.out.println("Successfully uploaded PDF: " + cloudinaryUrl);
            return cloudinaryUrl;

        } catch (IOException e) {
            System.err.println("Error converting HTML to PDF: " + e.getMessage());
            throw new RuntimeException("PDF conversion failed", e);
        } catch (Exception e) {
            System.err.println("Error uploading to Cloudinary: " + e.getMessage());
            throw new RuntimeException("Cloudinary upload failed", e);
        }
        finally {
            // Close resources in finally block
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    System.err.println("Error closing stream: " + e.getMessage());
                }
            }
        }
    }

}