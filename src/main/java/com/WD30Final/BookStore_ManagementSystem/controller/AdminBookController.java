package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.dto.BookDto;
import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import com.WD30Final.BookStore_ManagementSystem.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;

    @GetMapping
    public String listInventory(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        model.addAttribute("bookDto", new BookDto());
        return "admin/inventory";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("bookDto") BookDto bookDto, RedirectAttributes redirectAttributes) {
        try {
            bookService.createBook(bookDto);
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully with cover image!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute("bookDto") BookDto bookDto, RedirectAttributes redirectAttributes) {
        try {
            bookService.updateBook(id, bookDto);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/books";
    }
}