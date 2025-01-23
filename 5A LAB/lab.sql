-- Active: 1726174956295@@127.0.0.1@3306@5A
CREATE TABLE accounts (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    balance INT
);

-- Inserting sample data into the `accounts` table
INSERT INTO accounts (id, name, balance) VALUES (1, 'Account 1', 500);
INSERT INTO accounts (id, name, balance) VALUES (2, 'Account 2', 300);

CREATE TABLE customers (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    address VARCHAR(100)
);

-- Inserting sample data into the `customers` table
INSERT INTO customers (id, name, address) VALUES (1, 'John Doe', '456 Old Street');
INSERT INTO customers (id, name, address) VALUES (2, 'Jane Smith', '789 Elm Street');


CREATE TABLE products (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    quantity INT
);

-- Inserting sample data into the `products` table
INSERT INTO products (id, name, quantity) VALUES (101, 'Product A', 5);
INSERT INTO products (id, name, quantity) VALUES (102, 'Product B', 10);
