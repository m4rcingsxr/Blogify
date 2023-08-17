-- Insert statements for Category
INSERT INTO categories (id, name) VALUES (1, 'Technology');
INSERT INTO categories (id, name) VALUES (2, 'Health');
INSERT INTO categories (id, name) VALUES (3, 'Finance');
INSERT INTO categories (id, name) VALUES (4, 'Education');
INSERT INTO categories (id, name) VALUES (5, 'Lifestyle');

ALTER TABLE categories ALTER COLUMN id RESTART WITH 6;

-- Insert statements for Articles associated with Technology
INSERT INTO articles (id, title, description, content, category_id) VALUES (1, 'Latest Tech Trends', 'An overview of the latest trends in technology.', 'Content about latest tech trends...', 1);
INSERT INTO articles (id, title, description, content, category_id) VALUES (2, 'AI Innovations', 'Innovations in Artificial Intelligence.', 'Content about AI innovations...', 1);

-- Insert statements for Articles associated with Health
INSERT INTO articles (id, title, description, content, category_id) VALUES (3, 'Healthy Living Tips', 'Tips for a healthier lifestyle.', 'Content about healthy living tips...', 2);
INSERT INTO articles (id, title, description, content, category_id) VALUES (4, 'Nutrition Basics', 'Basic nutrition information.', 'Content about nutrition basics...', 2);

-- Insert statements for Articles associated with Finance
INSERT INTO articles (id, title, description, content, category_id) VALUES (5, 'Investing 101', 'An introduction to investing.', 'Content about investing 101...', 3);
INSERT INTO articles (id, title, description, content, category_id) VALUES (6, 'Saving for Retirement', 'Tips on saving for retirement.', 'Content about saving for retirement...', 3);

-- Insert statements for Articles associated with Education
INSERT INTO articles (id, title, description, content, category_id) VALUES (7, 'Online Learning Platforms', 'Overview of popular online learning platforms.', 'Content about online learning platforms...', 4);
INSERT INTO articles (id, title, description, content, category_id) VALUES (8, 'Study Tips', 'Tips for effective studying.', 'Content about study tips...', 4);

-- Insert statements for Articles associated with Lifestyle
INSERT INTO articles (id, title, description, content, category_id) VALUES (9, 'Travel on a Budget', 'How to travel without breaking the bank.', 'Content about budget travel...', 5);
INSERT INTO articles (id, title, description, content, category_id) VALUES (10, 'Minimalist Living', 'Benefits of living a minimalist lifestyle.', 'Content about minimalist living...', 5);

ALTER TABLE articles ALTER COLUMN id RESTART WITH 11;