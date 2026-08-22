package com.saga.shared.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${app.cloudinary.url:}") String cloudinaryUrl) {
        if (cloudinaryUrl != null && !cloudinaryUrl.isEmpty()) {
            this.cloudinary = new Cloudinary(cloudinaryUrl);
        } else {
            this.cloudinary = null; // Will just mock or throw if not configured
        }
    }

    public String uploadFile(byte[] fileData, String originalFilename) throws IOException {
        if (this.cloudinary == null) {
            System.err.println("Cloudinary URL not configured, returning mock URL");
            return "https://mock-cloudinary.com/" + originalFilename;
        }

        // Generate a random public ID to avoid collisions
        String publicId = "jira_attachments/" + UUID.randomUUID() + "_"
                + originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");

        Map uploadResult = cloudinary.uploader().upload(fileData, ObjectUtils.asMap(
                "public_id", publicId,
                "resource_type", "auto" // Automatically detect if it's image, video, or raw file
        ));

        return uploadResult.get("secure_url").toString();
    }
}
