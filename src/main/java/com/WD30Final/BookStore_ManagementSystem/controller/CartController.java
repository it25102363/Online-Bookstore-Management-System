package com.WD30Final.BookStore_ManagementSystem.controller;

import com.WD30Final.BookStore_ManagementSystem.entity.Order;
import com.WD30Final.BookStore_ManagementSystem.model.Cart;
import com.WD30Final.BookStore_ManagementSystem.services.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Cart cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        return "cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long bookId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            cartService.addBookToCart(session, bookId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Added book to your cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long bookId,
                                 @RequestParam int quantity,
                                 HttpSession session) {
        cartService.updateCartItem(session, bookId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam Long bookId, HttpSession session) {
        cartService.removeCartItem(session, bookId);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String email = (principal != null) ? principal.getName() : "guest@bookstore.com";
            String name = (principal != null) ? principal.getName() : "Guest Customer";

            Order order = cartService.checkout(session, email, name);
            redirectAttributes.addFlashAttribute("orderNumber", order.getOrderNumber());
            redirectAttributes.addFlashAttribute("checkoutSuccess", true);
            return "redirect:/cart?success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/checkout/buy-now")
    public String buyNow(@RequestParam Long bookId,
                         @RequestParam(defaultValue = "1") int quantity,
                         HttpSession session,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            Cart cart = cartService.getCart(session);
            cart.clear();
            cartService.addBookToCart(session, bookId, quantity);

            String email = (principal != null) ? principal.getName() : "guest@bookstore.com";
            String name = (principal != null) ? principal.getName() : "Guest Customer";

            Order order = cartService.checkout(session, email, name);
            redirectAttributes.addFlashAttribute("orderNumber", order.getOrderNumber());
            redirectAttributes.addFlashAttribute("checkoutSuccess", true);
            return "redirect:/cart?success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/books/" + bookId;
        }
    }
}