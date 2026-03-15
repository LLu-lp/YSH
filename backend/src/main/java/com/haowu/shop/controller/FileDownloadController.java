package com.haowu.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class FileDownloadController {

    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    @GetMapping("/avatar/{filename}")
    public ResponseEntity<Resource> downloadAvatar(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir, "avatar", filename).toAbsolutePath();
            
            // 安全检查：防止目录遍历攻击
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            if (!filePath.toAbsolutePath().startsWith(uploadPath)) {
                return ResponseEntity.notFound().build();
            }
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

