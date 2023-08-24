INSERT INTO customers (id, email, password, first_name, last_name) VALUES (1, 'adam.adams@example.com', '$2a$12$aCf07EJ8kS3DOWSH7z7rY.HYOeoTVUr.ht1iqqgDOeo9kaYr.l2VS', 'Adam', 'Adams');
INSERT INTO customers (id, email, password, first_name, last_name) VALUES (2, 'adam.alexander@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Adam', 'Alexander');
INSERT INTO customers (id, email, password, first_name, last_name) VALUES (3, 'helen.hart@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Helen', 'Hart');
INSERT INTO customers (id, email, password, first_name, last_name) VALUES (4, 'hannah.hughes@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Hannah', 'Hughes');
INSERT INTO customers (id, email, password, first_name, last_name) VALUES (5, 'ian.irving@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Ian', 'Irving');
INSERT INTO customers (id, email, password, first_name, last_name) VALUES (6, 'irene.ives@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Irene', 'Ives');
INSERT INTO customers (id, email, password, first_name, last_name) VALUES (7, 'ivan.innes@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Ivan', 'Innes');
INSERT INTO customers (id, email, password, first_name, last_name) VALUES (8, 'jack.johnson@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Jack', 'Johnson');
INSERT INTO customers (id, email, password, first_name, last_name) VALUES (9, 'Julia.james@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Julia', 'James');
INSERT INTO customers (id, email, password, first_name, last_name) VALUES (10, 'julia.jacobs@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Julia', 'Jacobs');



INSERT INTO roles (id, name, description) VALUES (1, 'ROLE_ADMIN', 'Administrator with full access');
INSERT INTO roles (id, name, description) VALUES (2, 'ROLE_EDITOR', 'Editor with access to edit content');
INSERT INTO roles (id, name, description) VALUES (3, 'ROLE_USER', 'Regular user with limited access');

insert into customers_roles (customer_id, role_id) values (1,1);
insert into customers_roles (customer_id, role_id) values (1,2);
insert into customers_roles (customer_id, role_id) values (1,3);

ALTER TABLE customers ALTER COLUMN id RESTART WITH 11;
