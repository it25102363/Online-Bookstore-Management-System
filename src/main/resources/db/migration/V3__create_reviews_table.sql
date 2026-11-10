
CREATE TABLE `bookstoresystem`.`reviews`(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    book_title      VARCHAR(150) NOT NULL,
    rating          INT          NOT NULL,
    comment         VARCHAR(1000) NOT NULL,
    customer_email  VARCHAR(150) NOT NULL,
    customer_name   VARCHAR(100) NOT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX idx_reviews_customer_email ON reviews (customer_email);
CREATE INDEX idx_reviews_created_at ON reviews (created_at);
