-- Active: 1726174956295@@127.0.0.1@3306@5A
CREATE TABLE accounts (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    balance INT
);

-- Inserting sample data into the `accounts` table
INSERT INTO accounts (id, name, balance) VALUES (1, 'Account 1', 5000);
INSERT INTO accounts (id, name, balance) VALUES (2, 'Account 2', 3000);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL
);

INSERT INTO orders (customer_id, product_id, quantity)
VALUES
(1, 101, 2),
(2, 102, 1);
