package com.limlydocx.service;

import com.cloudinary.Cloudinary;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.wml.Drawing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;

import org.springframework.http.ResponseEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;



import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Log4j2
public class DocumentService {


    private final Cloudinary cloudinary;
    private final GlobalVariable globalVariable;
    private final DocumentRepository documentRepository;



    /**
     * Saves document information in the database.
     *
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
     *
     * @param content        the HTML content
     * @param uniqueFileName the unique file name
     */
    public ResponseEntity<String> generatePdfAndUploadOnCloud(String content, String uniqueFileName) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

//        String path = "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\"+UUID.randomUUID().toString()+".pdf";
//        File file = new File(path);
//        try(OutputStream outputStream = new FileOutputStream(file)){

            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(content, byteArrayOutputStream, properties);

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

//            uploadToCloudinary(uniqueFileName, pdfBytes);

            byteArrayOutputStream.close();

            return ResponseEntity.ok("pdf created successfully");
        } catch (IOException e) {
            log.error("Error generating PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("PDF generation failed " + e.getMessage());
        }
    }



    public ResponseEntity<String> generateDocx(String htmlContent, String uniqueFilename) {
        System.out.println("HTML Content: " + htmlContent);

        // Define directory and file path
        String directoryPath = "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\";
        if (!directoryPath.endsWith(File.separator)) {
            directoryPath += File.separator;
        }
        String path = directoryPath + uniqueFilename;

        try {
            // Ensure the directory exists
            File directory = new File(directoryPath);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + directoryPath);
            }
            File file = new File(path);

            // Create a WordprocessingMLPackage and its main document part
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

            // Parse the HTML using Jsoup
            Document document = Jsoup.parse(htmlContent);

            // Process embedded images (base64) if present
            Elements imgElements = document.select("img[src^=data:image]");
            for (Element imgElement : imgElements) {
                String src = imgElement.attr("src");
                if (!src.contains(",")) continue;
                String[] parts = src.split(",", 2);
                if (parts.length < 2) continue;
                String base64Data = parts[1];
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);

                // Create image part and inline image
                BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imageBytes);
                Inline inline = imagePart.createImageInline("Image", "Generated Image", 1, 2, false);

                // Build the drawing element and wrap it in a run and paragraph
                Drawing drawing = new Drawing();
                drawing.getAnchorOrInline().add(inline);
                R run = new R();
                run.getContent().add(drawing);
                P paragraph = new P();
                paragraph.getContent().add(run);
                mainDocumentPart.addObject(paragraph);

                // Remove the <img> tag so it won't be processed again in the HTML conversion
                imgElement.remove();
            }

            // Normalize and clean the remaining HTML (text, formatting, etc.)
            // Optionally, replace <br> tags with newline markers if needed
            document.select("br").append("\\n");
            String cleanedHtml = document.html().replaceAll("\\s{2,}", " ").trim();

            // Import the cleaned XHTML into the DOCX document
            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
            mainDocumentPart.getContent().addAll(xhtmlImporter.convert(cleanedHtml, null));

            // Save the DOCX file
            try (OutputStream outputStream = new FileOutputStream(file)) {
                wordMLPackage.save(outputStream);
            }

            return ResponseEntity.ok("DOCX created successfully at: " + path);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating DOCX: " + e.getMessage());
        }
    }



    /**
     * Uploads the PDF/DOCX on Cloudinary storage .
     *
     * @param uniqueFileName the unique file name
     * @param documentByte     the PDF byte array
     */
    private void uploadToCloudinary(String uniqueFileName, byte[] documentByte) {
        Map<String, Object> options = new HashMap<>();
        options.put("resource_type", "raw");
        options.put("public_id", uniqueFileName);
        options.put("overwrite", true);
        options.put("tags", "pdf,document");

        try {
            cloudinary.uploader().upload(documentByte, options);
        } catch (IOException e) {
            log.error("Error uploading to Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }


}
