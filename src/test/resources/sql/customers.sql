INSERT INTO customers (id, email, password, first_name, last_name, enabled, account_locked, created_date, updated_date) VALUES (1, 'adam.adams@example.com', '$2a$12$aCf07EJ8kS3DOWSH7z7rY.HYOeoTVUr.ht1iqqgDOeo9kaYr.l2VS', 'Adam', 'Adams',1,1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customers (id, email, password, first_name, last_name, enabled, account_locked, created_date, updated_date) VALUES (2, 'adam.alexander@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Adam', 'Alexander',1,1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customers (id, email, password, first_name, last_name, enabled, account_locked, created_date, updated_date) VALUES (3, 'helen.hart@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Helen', 'Hart',1,1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customers (id, email, password, first_name, last_name, enabled, account_locked, created_date, updated_date) VALUES (4, 'hannah.hughes@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Hannah', 'Hughes',1,1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customers (id, email, password, first_name, last_name, enabled, account_locked, created_date, updated_date) VALUES (5, 'ian.irving@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Ian', 'Irving',1,1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customers (id, email, password, first_name, last_name, enabled, account_locked, created_date, updated_date) VALUES (6, 'irene.ives@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Irene', 'Ives',1,1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customers (id, email, password, first_name, last_name, enabled, account_locked, created_date, updated_date) VALUES (7, 'ivan.innes@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Ivan', 'Innes',1,1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customers (id, email, password, first_name, last_name, enabled, account_locked, created_date, updated_date) VALUES (8, 'jack.johnson@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Jack', 'Johnson',1,1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customers (id, email, password, first_name, last_name, enabled, account_locked, created_date, updated_date) VALUES (9, 'Julia.james@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Julia', 'James',1,1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customers (id, email, password, first_name, last_name, enabled, account_locked, created_date, updated_date) VALUES (10, 'julia.jacobs@example.com', '$2b$12$abcdefghijklnmnopqrstuvwxyz123456781234567890', 'Julia', 'Jacobs',1,1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

insert into customers_roles (customer_id, role_id) values (1,1);
insert into customers_roles (customer_id, role_id) values (1,2);
insert into customers_roles (customer_id, role_id) values (1,3);

ALTER TABLE customers ALTER COLUMN id RESTART WITH 11;
