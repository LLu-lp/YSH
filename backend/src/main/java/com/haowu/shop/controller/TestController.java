package com.haowu.shop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    @GetMapping("/upload-dir")
    public Map<String, Object> checkUploadDir() {
        Map<String, Object> result = new HashMap<>();
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            result.put("uploadDir", uploadDir);
            result.put("absolutePath", uploadPath.toString());
            result.put("exists", Files.exists(uploadPath));
            result.put("isDirectory", Files.isDirectory(uploadPath));
            
            // 列出 avatar 目录下的文件
            Path avatarPath = uploadPath.resolve("avatar");
            result.put("avatarPath", avatarPath.toString());
            result.put("avatarExists", Files.exists(avatarPath));
            
            if (Files.exists(avatarPath)) {
                result.put("avatarFiles", Files.list(avatarPath)
                        .map(p -> p.getFileName().toString())
                        .toArray());
            }
            
            logger.info("上传目录信息: {}", result);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            logger.error("检查上传目录失败", e);
        }
        return result;
    }
}

