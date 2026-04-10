package com.sanav.controller;

import com.sanav.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Please select a file to upload"));
        }

        try {
            // Create directory if not exists
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            Path path = Paths.get(UPLOAD_DIR + filename);

            // Save the file
            Files.write(path, file.getBytes());

            String fileUrl = "/uploads/" + filename;
            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", fileUrl));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Could not upload file: " + e.getMessage()));
        }
    }
}
