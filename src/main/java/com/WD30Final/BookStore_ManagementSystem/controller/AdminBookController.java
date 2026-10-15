package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.dto.BookDto;
import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.services.BookService;
import com.WD30Final.BookStore_ManagementSystem.services.DemoDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;
    private final DemoDataService demoDataService;
    private final String uploadDir = "./uploads/";

    @GetMapping
    public String listInventory(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("categories", bookService.getAllCategories());
        model.addAttribute("newBook", new BookDto());
        return "admin/inventory";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute BookDto bookDto,
                          @RequestParam(value = "coverImageFile", required = false) MultipartFile coverImageFile,
                          RedirectAttributes redirectAttributes) {
        try {
            if (coverImageFile != null && !coverImageFile.isEmpty()) {
                String savedFilename = saveCoverImage(coverImageFile);
                bookDto.setCoverImageUrl("/uploads/" + savedFilename);
            }
            bookService.createBook(bookDto);
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding book: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable Long id,
                             @ModelAttribute BookDto bookDto,
                             @RequestParam(value = "coverImageFile", required = false) MultipartFile coverImageFile,
                             RedirectAttributes redirectAttributes) {
        try {
            if (coverImageFile != null && !coverImageFile.isEmpty()) {
                String savedFilename = saveCoverImage(coverImageFile);
                bookDto.setCoverImageUrl("/uploads/" + savedFilename);
            }
            bookService.updateBook(id, bookDto);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating book: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting book: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/seed-demo")
    public String seedPresentationData(RedirectAttributes redirectAttributes) {
        try {
            int count = demoDataService.loadPresentationData();
            redirectAttributes.addFlashAttribute("successMessage", "Presentation demo data loaded! (" + count + " books added).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to load demo data: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    private String saveCoverImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }
}