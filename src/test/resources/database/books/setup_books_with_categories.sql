CREATE TABLE IF NOT EXISTS books (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    isbn VARCHAR(255),
    price DECIMAL(10, 2),
    description VARCHAR(255),
    cover_image VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS books_categories (
    book_id BIGINT,
    category_id BIGINT,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

INSERT INTO categories (id, name, description) VALUES (1, 'Category1', 'Category1 description')
    ON DUPLICATE KEY UPDATE name='Category1', description='Category1 description';

INSERT INTO categories (id, name, description) VALUES (2, 'Category2', 'Category2 description')
    ON DUPLICATE KEY UPDATE name='Category2', description='Category2 description';

INSERT INTO categories (id, name, description) VALUES (3, 'Category3', 'Category3 description')
    ON DUPLICATE KEY UPDATE name='Category3', description='Category3 description';

DELETE FROM books;

INSERT INTO books (id, title, author, isbn, price, description, cover_image)
VALUES (1, 'Book1', 'Author1', '111222333444555', 20.00, "description Book1", "cover_image1.jpg")
    ON DUPLICATE KEY UPDATE title='Book1', author='Author1', isbn='111222333444555', price=20.00, description="description Book1", cover_image="cover_image1.jpg";

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1)
    ON DUPLICATE KEY UPDATE book_id=1, category_id=1;

INSERT INTO books (id, title, author, isbn, price, description, cover_image)
VALUES (2, 'Book2', 'Author2', '111222333444556', 25.00, "description Book2", "cover_image2.jpg")
    ON DUPLICATE KEY UPDATE title='Book2', author='Author2', isbn='111222333444556', price=25.00, description="description Book2", cover_image="cover_image2.jpg";

INSERT INTO books_categories (book_id, category_id) VALUES (2, 1)
    ON DUPLICATE KEY UPDATE book_id=2, category_id=1;

INSERT INTO books (id, title, author, isbn, price, description, cover_image)
VALUES (3, 'Book3', 'Author3', '111222333444557', 30.00, "description Book3", "cover_image3.jpg")
    ON DUPLICATE KEY UPDATE title='Book3', author='Author3', isbn='111222333444557', price=30.00, description="description Book3", cover_image="cover_image3.jpg";

INSERT INTO books_categories (book_id, category_id) VALUES (3, 2)
    ON DUPLICATE KEY UPDATE book_id=3, category_id=2;

INSERT INTO books (id, title, author, isbn, price, description, cover_image)
VALUES (4, 'Book4', 'Author4', '111222333444558', 35.00, "description Book4", "cover_image4.jpg")
    ON DUPLICATE KEY UPDATE title='Book4', author='Author4', isbn='111222333444558', price=35.00, description="description Book4", cover_image="cover_image4.jpg";
