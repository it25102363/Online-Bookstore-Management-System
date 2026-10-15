CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      order_number VARCHAR(64) NOT NULL UNIQUE,
    customer_email VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255),
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED',
    created_at DATETIME NOT NULL
    );

CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           order_id BIGINT NOT NULL,
                                           book_id BIGINT NOT NULL,
                                           book_title VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_book FOREIGN KEY (book_id) REFERENCES books(id)
    );