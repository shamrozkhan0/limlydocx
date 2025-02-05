package com.limlydocx.service;

import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private GlobalVariable globalVariable;

    @Autowired
    private DocumentRepository documentRepository;



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



//    /**
//     * creates Pdf & sends to user frontend to download pdf
//     * @param content
//     */
//    public void generatePdf(String content, Model model){
//        String path =  "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\output.pdf";
//
//        try{
//            System.out.println(content); // for testing
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            HtmlConverter.convertToPdf(content, byteArrayOutputStream);
//
//            model.addAttribute("file", byteArrayOutputStream.toByteArray());
//
//            System.out.println("pdf is creatted successfully "); // for testing
//
//        } catch (Exception e) {
//            System.out.println("Error : " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//
//    }
//


}