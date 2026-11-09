-- Optional demonstration data. Run after at least one customer exists.
INSERT INTO wishlist_items (item_type, user_id, book_id, note, target_price, recipient_name)
SELECT 'STANDARD',
       (SELECT id FROM users WHERE user_type = 'CUSTOMER' ORDER BY id LIMIT 1),
       (SELECT id FROM books ORDER BY id LIMIT 1 OFFSET 0),
       'Read this on holiday', NULL, NULL;

INSERT INTO wishlist_items (item_type, user_id, book_id, note, target_price, recipient_name)
SELECT 'PRIORITY',
       (SELECT id FROM users WHERE user_type = 'CUSTOMER' ORDER BY id LIMIT 1),
       (SELECT id FROM books ORDER BY id LIMIT 1 OFFSET 1),
       'Buy when it is on sale', 15.00, NULL;

INSERT INTO wishlist_items (item_type, user_id, book_id, note, target_price, recipient_name)
SELECT 'GIFT',
       (SELECT id FROM users WHERE user_type = 'CUSTOMER' ORDER BY id LIMIT 1),
       (SELECT id FROM books ORDER BY id LIMIT 1 OFFSET 2),
       'Birthday present', NULL, 'Alex';
