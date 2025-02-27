package com.limlydocx.service;

import com.cloudinary.Cloudinary;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.Base64;
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

            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(content, byteArrayOutputStream, properties);

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();
            uploadToCloudinary(uniqueFileName, pdfBytes);

            byteArrayOutputStream.close();
            return ResponseEntity.ok("pdf created successfully");
        } catch (IOException e) {
            log.error("Error generating PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("PDF generation failed " + e.getMessage());
        }
    }



    /**
     *
     * @param htmlContent
     * @param uniqueFilename
     * @return
     */
    public ResponseEntity<String> generateDocx(String htmlContent, String uniqueFilename) {
        // Define the directory path
        String directoryPath = "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\";
        // Ensure the directory path ends with a separator
        if (!directoryPath.endsWith(File.separator)) {
            directoryPath += File.separator;
        }
        // Combine directory path with the unique filename
        String path = directoryPath + uniqueFilename;

        try {
            // Create a File object for the directory
            File directory = new File(directoryPath);
            // If the directory doesn't exist, create it
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    return ResponseEntity.status(500).body("Failed to create directory: " + directoryPath);
                }
            }

            // Create a File object for the output file
            File file = new File(path);

            // Create a WordprocessingMLPackage
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

            // Parse the HTML content
            Document document = Jsoup.parse(htmlContent);
            Elements imgElements = document.select("img[src^=data:image]");

            // Process each image
            for (Element imgElement : imgElements) {
                String src = imgElement.attr("src");
                String base64Data = src.split(",")[1];
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);

                // Create the image part in the DOCX
                BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imageBytes);

                // Create the inline image
                Inline inline = imagePart.createImageInline("Filename hint", "Alt text", 1, 2, false);

                // Add the inline image to a drawing
                Drawing drawing = new Drawing();
                drawing.getAnchorOrInline().add(inline);

                // Add the drawing to a run
                R run = new R();
                run.getContent().add(drawing);

                // Add the run to a paragraph
                P paragraph = new P();
                paragraph.getContent().add(run);

                // Add the paragraph to the main document part
                mainDocumentPart.addObject(paragraph);

                // Remove the original <img> tag
                imgElement.remove();
            }

            // Convert the modified HTML to XHTML
            String xhtml = document.html();

            // Convert XHTML to DOCX content
            org.docx4j.convert.in.xhtml.XHTMLImporterImpl xhtmlImporter = new org.docx4j.convert.in.xhtml.XHTMLImporterImpl(wordMLPackage);
            mainDocumentPart.getContent().addAll(xhtmlImporter.convert(xhtml, null));

            // Save the DOCX to the file system
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
