package com.limlydocx.service;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import com.limlydocx.enums.CloudinaryAPI;
import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadFile(File file) {
        try {
            String uniqueFileName = UUID.randomUUID().toString(); // Generate unique UUID filename
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.asMap(
                    "resource_type", "raw",  // "raw" ensures Cloudinary stores it as a file, not an image
                    "public_id", "documents/" + uniqueFileName, // Store inside 'documents/' folder
                    "overwrite", true
            ));
            return uploadResult.get("secure_url").toString(); // Return the Cloudinary URL
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
