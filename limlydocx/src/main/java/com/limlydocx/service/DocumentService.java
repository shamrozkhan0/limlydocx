package com.limlydocx.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.limlydocx.entity.DocumentEntity;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
public class DocumentService {

    @Autowired
    private GlobalVariable globalVariable;

    @Autowired
    private DocumentRepository documentRepository;



    public void saveDocumentInDatabase(String content, Authentication authentication) {
        String username = globalVariable.getUsername(authentication);
        System.out.println("This is the username " + username);//  for testing

        DocumentEntity savedocument = new DocumentEntity();
        savedocument.setContent(content);
        savedocument.setUploadOn(LocalDate.now());
        savedocument.setCreator(username);
        documentRepository.save(savedocument);
    }



    public void generatePdf(String content, RedirectAttributes redirectAttributes){
        String path =  "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\output.pdf";

        try{
            System.out.println(content);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(content, byteArrayOutputStream);

            redirectAttributes.addFlashAttribute("file", byteArrayOutputStream.toByteArray());

            System.out.println("pdf is creatted successfully ");

        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            throw new RuntimeException(e);
        }

    }



    }