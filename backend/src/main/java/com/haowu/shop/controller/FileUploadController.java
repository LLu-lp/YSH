package com.haowu.shop.controller;

import com.haowu.shop.service.UserService;
import com.haowu.shop.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Autowired
    private UserService userService;

    @PostMapping("/image")
    public ResponseUtil<Object> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseUtil.error("文件为空");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseUtil.error("只支持图片文件");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseUtil.error("文件大小不能超过5MB");
            }

            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            String fileUrl = "/uploads/" + filename;

            java.util.Map<String, String> result = new java.util.HashMap<>();
            result.put("url", fileUrl);
            return ResponseUtil.success("上传成功", result);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.error("上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/avatar")
    public ResponseUtil<Object> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseUtil.error("文件为空");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseUtil.error("只支持图片文件");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseUtil.error("文件大小不能超过5MB");
            }

            // 创建头像上传目录
            Path avatarPath = Paths.get(uploadDir, "avatar");
            Files.createDirectories(avatarPath);

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            Path filePath = avatarPath.resolve(filename);
            // 直接写入文件，不使用 transferTo()
            Files.write(filePath, file.getBytes());

            String fileUrl = "/uploads/avatar/" + filename;

            java.util.Map<String, String> result = new java.util.HashMap<>();
            result.put("url", fileUrl);
            return ResponseUtil.success("上传成功", result);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.error("上传失败：" + e.getMessage());
        }
    }
}
