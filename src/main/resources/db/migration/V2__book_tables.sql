-- Base Item table for joined-table inheritance
CREATE TABLE IF NOT EXISTS items (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    item_type VARCHAR(50) NOT NULL
    );

-- Books table inheriting from items
CREATE TABLE IF NOT EXISTS books (
                                     id BIGINT PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(50) NOT NULL UNIQUE,
    category VARCHAR(100),
    description TEXT,
    cover_image_url VARCHAR(500),
    rating DOUBLE DEFAULT 0.0,
    CONSTRAINT fk_book_item FOREIGN KEY (id) REFERENCES items(id) ON DELETE CASCADE
    );

-- Optional sample books so the storefront isn't empty on launch
INSERT INTO items (id, price, stock, item_type) VALUES
                                                    (1, 19.99, 12, 'BOOK'),
                                                    (2, 24.50, 4, 'BOOK'),
                                                    (3, 14.95, 0, 'BOOK');

INSERT INTO books (id, title, author, isbn, category, description, cover_image_url, rating) VALUES
                                                                                                (1, 'Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', '978-0439708180', 'Fantasy', 'The story of a young wizard entering Hogwarts.', '/images/harry.jpg', 4.8),
                                                                                                (2, 'Diary of a Wimpy Kid', 'Jeff Kinney', '978-0141324906', 'Fiction', 'Hilarious adventures of Greg Heffley in middle school.', '/images/wimpy.jpg', 4.5),
                                                                                                (3, 'Clean Architecture', 'Robert C. Martin', '978-0134494166', 'Technology', 'A Craftsman Guide to Software Structure and Design.', '/images/books.jpg', 4.9);