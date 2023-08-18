INSERT INTO articles (id, title, description, content)
VALUES (1, 'Introduction to Java', 'A beginner''s guide to Java programming', 'Java is a high-level, class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible.');

INSERT INTO articles (id, title, description, content)
VALUES (2, 'Spring Boot Overview', 'Understanding the basics of Spring Boot', 'Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".');

INSERT INTO articles (id, title, description, content)
VALUES (3, 'Hibernate ORM', 'An introduction to Hibernate ORM', 'Hibernate ORM enables developers to more easily write applications whose data outlives the application process.');

INSERT INTO articles (id, title, description, content)
VALUES (4, 'RESTful Web Services', 'Creating RESTful web services with Spring Boot', 'RESTful Web Services are an architectural style for creating web services that are maintainable and scalable.');

INSERT INTO articles (id, title, description, content)
VALUES (5, 'Microservices Architecture', 'Introduction to Microservices Architecture', 'Microservices - also known as the microservice architecture - is an architectural style that structures an application as a collection of services that are highly maintainable and testable.');

INSERT INTO articles (id, title, description, content)
VALUES (6, 'Docker for Developers', 'Getting started with Docker', 'Docker is an open platform for developing, shipping, and running applications. Docker enables you to separate your applications from your infrastructure so you can deliver software quickly.');

INSERT INTO articles (id, title, description, content)
VALUES (7, 'Kubernetes Basics', 'Understanding Kubernetes basics', 'Kubernetes is an open-source system for automating the deployment, scaling, and management of containerized applications.');

INSERT INTO articles (id, title, description, content)
VALUES (8, 'Continuous Integration', 'Best practices for Continuous Integration', 'Continuous Integration is a development practice where developers integrate code into a shared repository frequently, preferably several times a day.');

INSERT INTO articles (id, title, description, content)
VALUES (9, 'Unit Testing with JUnit', 'Writing unit tests with JUnit', 'JUnit is a simple framework to write repeatable tests. It is an instance of the xUnit architecture for unit testing frameworks.');

INSERT INTO articles (id, title, description, content)
VALUES (10, 'Effective Git', 'Tips and tricks for using Git effectively', 'Git is a distributed version-control system for tracking changes in source code during software development.');

INSERT INTO comments (id, full_name, content, article_id)
VALUES (1,'John Doe', 'Great introduction to Java!', 1);

INSERT INTO comments (id, full_name, content, article_id)
VALUES (2,'Jane Smith', 'Very helpful overview of Spring Boot.', 2);

INSERT INTO comments (id, full_name, content, article_id)
VALUES (3,'Alice Johnson', 'Hibernate ORM explained well.', 3);

INSERT INTO comments (id, full_name, content, article_id)
VALUES (4,'Bob Brown', 'RESTful Web Services are the future!', 4);

INSERT INTO comments (id, full_name, content, article_id)
VALUES (5,'Charlie Davis', 'Microservices Architecture demystified.', 5);

INSERT INTO comments (id, full_name, content, article_id)
VALUES (6,'Eve Evans', 'Docker is indeed powerful.', 6);

INSERT INTO comments (id, full_name, content, article_id)
VALUES (7,'Frank Green', 'Kubernetes is essential for modern dev.', 7);

INSERT INTO comments (id, full_name, content, article_id)
VALUES (8,'Grace Hall', 'Continuous Integration is a must-know.', 8);

INSERT INTO comments (id, full_name, content, article_id)
VALUES (9,'Henry Young', 'JUnit makes unit testing easier.', 9);

INSERT INTO comments (id, full_name, content, article_id)
VALUES (10,'Ivy King', 'Git tips are very useful, thanks!', 10);

ALTER TABLE articles ALTER COLUMN id RESTART WITH 11;
ALTER TABLE comments ALTER COLUMN id RESTART WITH 11;