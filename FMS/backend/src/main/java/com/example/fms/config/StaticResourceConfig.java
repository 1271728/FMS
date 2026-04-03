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
        List<Path> bases = new ArrayList<>();
        bases.add(userDir);
        if (userDir.getParent() != null) bases.add(userDir.getParent().normalize());
        if (userDir.getParent() != null && userDir.getParent().getParent() != null) {
            bases.add(userDir.getParent().getParent().normalize());
        }

        for (Path base : bases) {
            locations.add(base.resolve("uploads").toUri().toString());
            locations.add(base.resolve("backend").resolve("uploads").toUri().toString());
            locations.add(base.resolve("FMS").resolve("uploads").toUri().toString());
            locations.add(base.resolve("FMS").resolve("backend").resolve("uploads").toUri().toString());
        }

        registry.addResourceHandler("/uploads/**").addResourceLocations(locations.toArray(new String[0]));
    }
}
