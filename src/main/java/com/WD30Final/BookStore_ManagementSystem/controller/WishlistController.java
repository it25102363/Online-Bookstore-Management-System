package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.dto.WishlistItemForm;
import com.WD30Final.BookStore_ManagementSystem.entity.WishlistItem;
import com.WD30Final.BookStore_ManagementSystem.services.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {
    private static final String REDIRECT_WISHLIST = "redirect:/wishlist";
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public String viewWishlist(@RequestParam(defaultValue = "ALL") String type,
                               @RequestParam(defaultValue = "priority") String sort,
                               Authentication authentication,
                               Model model) {
        String email = authentication.getName();
        List<WishlistItem> allItems = wishlistService.getWishlist(email, "ALL", sort);
        List<WishlistItem> shownItems = "ALL".equalsIgnoreCase(type)
                ? allItems
                : wishlistService.getWishlist(email, type, sort);
        BigDecimal totalValue = allItems.stream()
                .map(item -> item.getBook().getPrice())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long mustReadCount = allItems.stream()
                .filter(item -> "PRIORITY".equals(item.getTypeCode()))
                .count();
        model.addAttribute("wishlistItems", shownItems);
        model.addAttribute("wishlistCount", allItems.size());
        model.addAttribute("wishlistTotal", totalValue);
        model.addAttribute("mustReadCount", mustReadCount);
        model.addAttribute("type", type.toUpperCase());
        model.addAttribute("sort", sort);
        return "wishlist";
    }

    @GetMapping("/add")
    public String addPage(@RequestParam(required = false) Long bookId,
                          Authentication authentication,
                          Model model) {
        if (!model.containsAttribute("form")) {
            WishlistItemForm form = new WishlistItemForm();
            form.setBookId(bookId);
            model.addAttribute("form", form);
        }
        model.addAttribute("books", wishlistService.getBooksNotInWishlist(authentication.getName()));
        return "wishlist-add";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute WishlistItemForm form,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        try {
            wishlistService.addItem(authentication.getName(), form);
            redirectAttributes.addFlashAttribute("message", "Book added to your wishlist.");
            return REDIRECT_WISHLIST;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/wishlist/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Authentication authentication,
                           Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("item", wishlistService.getItem(authentication.getName(), id));
            return "wishlist-edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return REDIRECT_WISHLIST;
        }
    }

    @PostMapping("/edit/{id}")
    public String updateItem(@PathVariable Long id, @ModelAttribute WishlistItemForm form,
                             Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            wishlistService.updateItem(authentication.getName(), id, form);
            redirectAttributes.addFlashAttribute("message", "Wishlist item updated.");
            return REDIRECT_WISHLIST;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/wishlist/edit/" + id;
        }
    }

    @PostMapping("/remove/{id}")
    public String removeItem(@PathVariable Long id, Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            wishlistService.removeItem(authentication.getName(), id);
            redirectAttributes.addFlashAttribute("message", "Book removed from your wishlist.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return REDIRECT_WISHLIST;
    }

    @PostMapping("/clear")
    public String clear(Authentication authentication, RedirectAttributes redirectAttributes) {
        wishlistService.clear(authentication.getName());
        redirectAttributes.addFlashAttribute("message", "Your wishlist has been cleared.");
        return REDIRECT_WISHLIST;
    }

    @PostMapping("/toggle/{bookId}")
    public String toggle(@PathVariable Long bookId, Authentication authentication,
                         HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            boolean added = wishlistService.toggle(authentication.getName(), bookId);
            redirectAttributes.addFlashAttribute("message",
                    added ? "Book added to your wishlist." : "Book removed from your wishlist.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirectBack(request);
    }

    private String redirectBack(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URI uri = URI.create(referer);
                String path = uri.getRawPath();
                boolean sameHost = request.getServerName().equalsIgnoreCase(uri.getHost());
                if (sameHost && path != null && path.startsWith("/") && !path.startsWith("//")) {
                    return "redirect:" + (uri.getRawQuery() != null ? path + "?" + uri.getRawQuery() : path);
                }
            } catch (IllegalArgumentException ignored) { }
        }
        return REDIRECT_WISHLIST;
    }
}
