package com.WD30Final.BookStore_ManagementSystem.dto;

import com.WD30Final.BookStore_ManagementSystem.entity.Book;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private String category;
    private String description;
    private String coverImageUrl;
    private Double rating;

    private MultipartFile coverImageFile;

    public static BookDto fromEntity(Book book) {
        if (book == null) return null;
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .stock(book.getStock())
                .category(book.getCategory())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .rating(book.getRating())
                .build();
    }

    public Book toEntity() {
        Book book = new Book();
        book.setId(this.id);
        book.setTitle(this.title);
        book.setAuthor(this.author);
        book.setIsbn(this.isbn);
        book.setPrice(this.price);
        book.setStock(this.stock != null ? this.stock : 0);
        book.setCategory(this.category);
        book.setDescription(this.description);
        book.setCoverImageUrl(this.coverImageUrl);
        book.setRating(this.rating != null ? this.rating : 0.0);
        book.setItemType("BOOK");
        return book;
    }


}