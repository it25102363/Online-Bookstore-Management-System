package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.entity.*;

public final class WishlistItemFactory {
    private WishlistItemFactory() { }

    public static WishlistItem create(String typeCode) {
        String code = typeCode == null || typeCode.isBlank()
                ? "STANDARD"
                : typeCode.trim().toUpperCase();
        return switch (code) {
            case "STANDARD" -> new StandardWishlistItem();
            case "PRIORITY" -> new PriorityWishlistItem();
            case "GIFT" -> new GiftWishlistItem();
            default -> throw new IllegalArgumentException("Unknown wishlist type: " + typeCode);
        };
    }
}
