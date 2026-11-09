CREATE TABLE IF NOT EXISTS wishlist_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_type VARCHAR(31) NOT NULL,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    note VARCHAR(255) NULL,
    target_price DECIMAL(10, 2) NULL,
    recipient_name VARCHAR(100) NULL,
    added_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_wishlist_user_book UNIQUE (user_id, book_id),
    CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
);
