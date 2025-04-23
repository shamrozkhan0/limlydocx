package com.limlydocx.service;

import com.cloudinary.Cloudinary;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.entity.User;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.EditorRepository;
import com.limlydocx.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Drawing;
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
import java.util.*;

@Service
@AllArgsConstructor
@Log4j2
public class EditorService {

    private final Cloudinary cloudinary;
    private final GlobalVariable globalVariable;
    private final EditorRepository documentRepository;
    private final UserRepository userRepository;
//    private final User user;

    private static final String DOCUMENT_STORAGE_PATH = "E:/limlydocx/limlydocx/src/main/resources/testPdf/";






    /**
     * Saves document metadata in the database.
     *
     * @param uniqueFileName The unique file name
     * @param authentication User authentication details
     */
    public void saveDocumentInDatabase(String uniqueFileName, Authentication authentication) {

        String username = globalVariable.getUsername(authentication);

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFileName(uniqueFileName);
        documentEntity.setUploadOn(LocalDate.now());
        documentEntity.setCreator(username);

        try {
            documentRepository.save(documentEntity);
            log.info("Document saved successfully: {}", uniqueFileName);
        } catch (Exception e) {
            log.error("Error saving document to database: {}", e.getMessage());
            throw new RuntimeException("Database save failed", e);
        }
    }



    /**
     * Generates a PDF from HTML content and uploads it to Cloudinary.
     *
     * @param content        The HTML content
     * @param uniqueFileName The unique file name
     * @return Response entity indicating success or failure
     */
    public ResponseEntity<String> generatePdfAndUploadOnCloud(String content, String uniqueFileName) {

        File directory = new File(DOCUMENT_STORAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs(); // create the directory if it doesn't exist
        }


        File file = new File(DOCUMENT_STORAGE_PATH + uniqueFileName);
        System.out.println(DOCUMENT_STORAGE_PATH + uniqueFileName);
//        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

        try (OutputStream outputStream = new FileOutputStream(file)) {

            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(content, outputStream, properties);


//            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

//            checkIfEditorFileExist();

//            uploadToCloudinary(uniqueFileName, pdfBytes);

            return ResponseEntity.ok("PDF created successfully");
        } catch (IOException e) {
            log.error("Error generating PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("PDF generation failed: " + e.getMessage());
        }
    }



    /**
     * Generates a DOCX file from HTML content.
     *
     * @param htmlContent    The HTML content
     * @param uniqueFilename The unique file name
     * @return Response entity indicating success or failure
     */
    public ResponseEntity<String> generateDocxFile(String htmlContent, String uniqueFilename) {
        String filePath = DOCUMENT_STORAGE_PATH + File.separator + uniqueFilename;

        try {
            File directory = new File(DOCUMENT_STORAGE_PATH);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + DOCUMENT_STORAGE_PATH);
            }
            File file = new File(filePath);

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

            Document document = Jsoup.parse(htmlContent);

            document.select("br").append("\\n");
            String cleanedHtml = document.html().replaceAll("\\s{2,}", " ").trim();

            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
            mainDocumentPart.getContent().addAll(xhtmlImporter.convert(cleanedHtml, null));
            processBase64Images(document, wordMLPackage, mainDocumentPart);

            try (OutputStream outputStream = new FileOutputStream(file)) {
                wordMLPackage.save(outputStream);
            }

//            byte[] fileByte = this.readFileToByteArray(file);
//            uploadToCloudinary(uniqueFilename, fileByte);

            return ResponseEntity.ok("DOCX created successfully");
        } catch (Exception e) {
            log.error("Error creating DOCX: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("DOCX creation failed: " + e.getMessage());
        }
    }



    /**
     * Processes base64 images embedded in HTML and embeds them into DOCX.
     *
     * @param document         The parsed HTML document
     * @param wordMLPackage    The WordprocessingMLPackage
     * @param mainDocumentPart The main document part
     * @throws Exception In case of processing errors
     */
    private void processBase64Images(Document document, WordprocessingMLPackage wordMLPackage, MainDocumentPart mainDocumentPart) throws Exception {
        Elements imgElements = document.select("img[src^=data:image]");
        for (Element imgElement : imgElements) {
            String src = imgElement.attr("src");
            if (!src.contains(",")) continue;

            String base64Data = src.split(",", 2)[1];
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imageBytes);
            Inline inline = imagePart.createImageInline("Image", "Generated Image", 1, 2, false);

            Drawing drawing = new Drawing();
            drawing.getAnchorOrInline().add(inline);
            R run = new R();
            run.getContent().add(drawing);
            P paragraph = new P();
            paragraph.getContent().add(run);
            mainDocumentPart.addObject(paragraph);

            imgElement.remove();
        }
    }



    /**
     * Uploads the document to Cloudinary.
     *
     * @param uniqueFileName The unique file name
     * @param documentBytes  The document content as byte array
     */
    private void uploadToCloudinary(String uniqueFileName, byte[] documentBytes) {
        Map<String, Object> options = new HashMap<>();
        options.put("resource_type", "raw");
        options.put("public_id", uniqueFileName);
        options.put("overwrite", true);
        options.put("tags", "document");

        try {
            cloudinary.uploader().upload(documentBytes, options);
            log.info("Document uploaded to Cloudinary: {}", uniqueFileName);
        } catch (IOException e) {
            log.error("Cloudinary upload failed: {}", e.getMessage());
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }



    /**
     * ONLY FOR DOCX
     * Reads a file and converts it into a byte array.
     *
     * @param file The file to be read
     * @return Byte array representation of the file
     * @throws IOException If an error occurs during reading
     */
    private byte[] readFileToByteArray(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }



    public void checkIfEditorFileExist(UUID EDITOR_ID){
        documentRepository.findEditorFileById(EDITOR_ID).ifPresentOrElse(
                ifpresent ->{
                    log.info("Existed File - Re-uploading the file");
                },

                () ->{
                    log.info("New File - cloudinary will take this as a new file");
                }


        );
}

}