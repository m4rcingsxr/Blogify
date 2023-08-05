INSERT INTO customers (id,email, password, first_name, last_name) VALUES (1, 'john.doe@example.com', 'password123', 'John', 'Doe');
INSERT INTO customers (id,email, password, first_name, last_name) VALUES (2, 'jane.smith@example.com', 'password123', 'Jane', 'Smith');
INSERT INTO customers (id,email, password, first_name, last_name) VALUES (3, 'alice.jones@example.com', 'password123', 'Alice', 'Jones');
INSERT INTO customers (id,email, password, first_name, last_name) VALUES (4, 'bob.brown@example.com', 'password123', 'Bob', 'Brown');

INSERT INTO roles (id, name, description) VALUES (1, 'ROLE_ADMIN', 'Administrator with full access');
INSERT INTO roles (id, name, description) VALUES (2, 'ROLE_EDITOR', 'Editor with access to edit content');
INSERT INTO roles (id, name, description) VALUES (3, 'ROLE_USER', 'Regular user with limited access');

insert into customers_roles (customer_id, role_id) values (1,1);
insert into customers_roles (customer_id, role_id) values (1,2);
insert into customers_roles (customer_id, role_id) values (1,3);

ALTER TABLE customers ALTER COLUMN id RESTART WITH 5;
