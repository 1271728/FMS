package com.example.fms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path userDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        String primary = userDir.resolve("uploads").toUri().toString();
        Path parent = userDir.getParent();
        if (parent == null) {
            registry.addResourceHandler("/uploads/**").addResourceLocations(primary);
            return;
        }
        String legacy = parent.resolve("uploads").toUri().toString();
        registry.addResourceHandler("/uploads/**").addResourceLocations(primary, legacy);
    }
}
