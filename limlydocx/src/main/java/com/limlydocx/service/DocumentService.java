package com.limlydocx.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class DocumentService {



    // This project uses iText, licensed under the AGPL.
    // You can find the source code here: <your-repo-link>



    @Autowired
    private GlobalVariable globalVariable;

    @Autowired
    private DocumentRepository documentRepository;



    public void saveDocument(String content, Authentication authentication) {

        String username = globalVariable.getUsername(authentication);
        System.out.println("This is the username " + username);//  for testing

        System.out.println("Impure : " +content);

        DocumentEntity savedocument = new DocumentEntity();
        savedocument.setContent(content);
        savedocument.setUploadOn(LocalDate.now());
        savedocument.setCreator(username);
        documentRepository.save(savedocument);

        generatePdf(content);
    }



    public void generatePdf(String content){
        String path = "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\" + UUID.randomUUID().toString() +".pdf";

        try{

            System.out.println(content);

            FileOutputStream pdfOutputStream = new FileOutputStream(path);
            HtmlConverter.convertToPdf(content,pdfOutputStream);
            System.out.println("pdf is creatted successfully ");

        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            throw new RuntimeException(e);
        }

    }







    }