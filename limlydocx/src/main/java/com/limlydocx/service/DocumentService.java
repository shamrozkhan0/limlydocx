package com.limlydocx.service;

import com.cloudinary.Cloudinary;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.hibernate.result.Output;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Base64;
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

        String path = "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\" + uniqueFileName + ".pdf";
        File file = new File(path);

//        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

        try (OutputStream outputStream = new FileOutputStream(file)) {

            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(content, outputStream, properties);
//            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

//            uploadToCloudinary(uniqueFileName, pdfBytes);

//            byteArrayOutputStream.close();
            outputStream.close();
            return ResponseEntity.ok("pdf created successfully");
        } catch (IOException e) {
            log.error("Error generating PDF: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("PDF generation failed " + e.getMessage());
        }
    }

    public ResponseEntity<String> generateDocxAndUploadOnCloud(String content, String uniqueFileName) {
        // Define the output path
        String directoryPath = "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\";
        String filePath = directoryPath + uniqueFileName + ".docx";

        // Create directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean dirCreated = directory.mkdirs();
            if (!dirCreated) {
                log.error("Failed to create directory: {}", directoryPath);
                return ResponseEntity.badRequest().body("Error creating output directory");
            }
        }

        File file = new File(filePath);

        try (OutputStream outputStream = new FileOutputStream(file)) {
            // Create a new Word document
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mainPart = wordPackage.getMainDocumentPart();

            // Parse HTML content using JSoup
            // Use XML parser and enforce XHTML syntax
            Document doc = Jsoup.parse(content, "", Parser.xmlParser());
            doc.outputSettings()
                    .syntax(Document.OutputSettings.Syntax.xml)
                    .escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml)
                    .prettyPrint(false);

            // Process images in the HTML
            Elements imgs = doc.select("img");
            processImages(imgs, wordPackage);

            // Convert XHTML to DOCX content
            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordPackage);
            xhtmlImporter.setHyperlinkStyle("Hyperlink");

            // Clear existing content and add converted content
            mainPart.getContent().clear();
            mainPart.getContent().addAll(xhtmlImporter.convert(doc.html(), null));

            // Save the document
            wordPackage.save(outputStream);

            log.info("DOCX document created successfully: {}", filePath);
            return ResponseEntity.ok("DOCX document created successfully");

        } catch (Exception e) {
            log.error("Failed to generate DOCX", e);
            return ResponseEntity.badRequest().body("Error creating DOCX: " + e.getMessage());
        }
    }

    /**
     * Process all images in the HTML and convert them to DOCX image parts
     *
     * @param imgs The collection of image elements from HTML
     * @param wordPackage The Word document package
     */
    private void processImages(Elements imgs, WordprocessingMLPackage wordPackage) {
        for (Element img : imgs) {
            String src = img.attr("src");
            if (src.startsWith("data:image/")) {
                try {
                    // Extract base64 data from the data URI
                    String[] parts = src.split(",", 2);
                    if (parts.length < 2) {
                        log.warn("Invalid image data URI format");
                        continue;
                    }

                    String base64Data = parts[1];
                    byte[] imageBytes = Base64.getDecoder().decode(base64Data);

                    // Add image to DOCX
                    BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordPackage, imageBytes);

                    // Get a unique ID for the image
                    String partName = imagePart.getPartName().getName();
                    String filename = partName.substring(partName.lastIndexOf("/") + 1);

                    // Update the src attribute in HTML to reference the DOCX image
                    img.attr("src", filename);

                    log.debug("Processed image: {}", filename);
                } catch (Exception ex) {
                    log.warn("Failed to process image: {}", ex.getMessage());
                }
            }
        }
    }

    /**
     * Uploads the PDF byte array to Cloudinary.
     *
     * @param uniqueFileName the unique file name
     * @param pdfBytes       the PDF byte array
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


//    public void generatesDocxAndUploadToCloud(String content, String uniqueFileName) {
//
//
//        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
//
//            // Create a new WordprocessingMLPackage (a DOCX file)
//            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
//
//            // Convert HTML content to DOCX using XHTMLImporterImpl
//
//            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
//
//            // Convert the HTML string content into a list of WordML objects
//            List<Object> convertedContent = xhtmlImporter.convert(updatedContent, null);
//
//            // Add the converted content to the main document part
//             wordMLPackage.getMainDocumentPart().getContent().addAll(convertedContent);
//
//             wordMLPackage.save(outputStream);
//
//             wordMLPackage.save(byteArrayOutputStream);
//
//             byte[] docxByte = byteArrayOutputStream.toByteArray();
//
//             uploadToCloudinary(uniqueFileName, docxByte);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
