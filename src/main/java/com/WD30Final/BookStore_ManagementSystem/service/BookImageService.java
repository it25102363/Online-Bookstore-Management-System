
package com.WD30Final.BookStore_ManagementSystem.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class BookImageService {

    private final Path folder = Paths.get("uploads", "books")
            .toAbsolutePath().normalize();

    private static final long MAX_SIZE = 5L * 1024 * 1024;

    public String save(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            return "";
        }

        if (image.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "Cover must be 5 MB or smaller."
            );
        }

        try {
            BufferedImage decoded;

            try (InputStream input = image.getInputStream()) {
                decoded = ImageIO.read(input);
            }

            if (decoded == null ||
                    decoded.getWidth() > 5000 ||
                    decoded.getHeight() > 5000) {
                throw new IllegalArgumentException(
                        "Upload a valid JPG or PNG (max 5000px per side)."
                );
            }

            String type = image.getContentType();
            String extension;

            if ("image/jpeg".equals(type)) {
                extension = ".jpg";
            } else if ("image/png".equals(type)) {
                extension = ".png";
            } else {
                throw new IllegalArgumentException(
                        "Only JPG and PNG images are supported."
                );
            }

            if (extension.equals(".jpg") &&
                    decoded.getType() != BufferedImage.TYPE_INT_RGB) {

                BufferedImage rgb = new BufferedImage(
                        decoded.getWidth(),
                        decoded.getHeight(),
                        BufferedImage.TYPE_INT_RGB
                );

                Graphics2D graphics = rgb.createGraphics();
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
                graphics.drawImage(decoded, 0, 0, null);
                graphics.dispose();

                decoded = rgb;
            }

            Files.createDirectories(folder);

            String filename = UUID.randomUUID() + extension;
            Path destination = folder.resolve(filename);

            boolean saved = ImageIO.write(
                    decoded,
                    extension.equals(".png") ? "png" : "jpg",
                    destination.toFile()
            );

            if (!saved) {
                throw new IOException("Image encoder unavailable.");
            }

            return filename;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not save cover image.", e
            );
        }
    }

    public void delete(String filename) {
        if (filename == null ||
                !filename.matches("[a-f0-9-]{36}\\.(jpg|png)")) {
            return;
        }

        try {
            Files.deleteIfExists(folder.resolve(filename));
        } catch (IOException ignored) {
            // Book information is still preserved.
        }
    }
}
