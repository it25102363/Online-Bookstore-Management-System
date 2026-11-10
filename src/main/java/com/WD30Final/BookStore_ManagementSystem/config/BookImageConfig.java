
package com.WD30Final.BookStore_ManagementSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class BookImageConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path uploadFolder = Paths.get("uploads", "books")
                .toAbsolutePath()
                .normalize();

        registry.addResourceHandler("/book-covers/**")
                .addResourceLocations(uploadFolder.toUri().toString());
    }
}
