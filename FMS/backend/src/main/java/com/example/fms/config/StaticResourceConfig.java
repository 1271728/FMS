package com.example.fms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path userDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Set<String> locations = new LinkedHashSet<>();
        locations.add(userDir.resolve("uploads").toUri().toString());
        locations.add(userDir.resolve("backend").resolve("uploads").toUri().toString());

        Path parent = userDir.getParent();
        if (parent != null) {
            locations.add(parent.resolve("uploads").toUri().toString());
            locations.add(parent.resolve("backend").resolve("uploads").toUri().toString());
        }

        List<String> resourceLocations = new ArrayList<>(locations);
        registry.addResourceHandler("/uploads/**").addResourceLocations(resourceLocations.toArray(new String[0]));
    }
}
