package com.haowu.shop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            // 获取上传目录的绝对路径
            String uploadPath = Paths.get(uploadDir).toAbsolutePath().toString();
            String resourceLocation = "file:" + uploadPath + "/";
            
            logger.info("配置静态资源映射: /uploads/** -> {}", resourceLocation);
            
            // 配置上传文件的访问路径
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(resourceLocation)
                    .setCachePeriod(3600);
        } catch (Exception e) {
            logger.error("配置静态资源映射失败", e);
        }
    }
}



