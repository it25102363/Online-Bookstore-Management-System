CREATE TABLE IF NOT EXISTS book_reviews (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            book_id BIGINT NOT NULL,
                                            rating INT NOT NULL,
                                            reviewer_name VARCHAR(255) NOT NULL,
    comment TEXT NOT NULL,
    created_at DATETIME,
    CONSTRAINT fk_review_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
    );

-- Optional starter sample reviews
INSERT INTO book_reviews (book_id, rating, reviewer_name, comment, created_at) VALUES
                                                                                   (1, 5, 'Hermione G.', 'An absolute childhood masterpiece. Captivating from page one.', NOW()),
                                                                                   (2, 4, 'Rowley J.', 'Super funny and lighthearted read. Greg is hilarious.', NOW()),
                                                                                   (3, 5, 'Linus T.', 'Essential reading for any software engineer. Great architecture principles.', NOW());