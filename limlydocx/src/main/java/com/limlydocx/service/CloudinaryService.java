package com.limlydocx.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

//    private final Cloudinary cloudinary;
//
//    public CloudinaryService(
//            @Value("${cloud.name}") String cloudName,
//            @Value("$api.key}") String apiKey,
//            @Value("${api.secret}") String apiSecret) {
//        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
//                "cloud_name", cloudName,
//                "api_key", apiKey,
//                "api_secret", apiSecret
//        ));
//    }
//
//    public void uploadDocumentFileInCloudinary(byte [] pdfBytes , UUID uniqueFileName){
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }



}
