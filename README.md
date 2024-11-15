# Blogify REST API

### Overview

Blogify is a REST API built using Spring and Spring Boot, providing complete functionality for managing a
blog application. The API supports full CRUD operations for articles, comments,
categories, and users. Blogify leverages OpenAPI and Swagger for seamless API documentation and integration, making it
developer-friendly and easy to explore. Security is ensured with JWT-based authorization, offering robust access control
to protect endpoints. The API supports querying with pagination, filtering, and sorting capabilities, making it a
flexible and scalable solution for blog management needs.

### Technology

- Spring Boot
- JWT
- SpringDoc OpenAPI and Swagger UI
- Lombok
- MySQL
- H2 Database
- Junit

### Features

- Full CRUD operations for articles, categories, comments, and customers.
- JWT-based authentication for secure API access.
- Role-based access control (RBAC) for different endpoints.
- OpenAPI (Swagger UI) documentation for easy API testing and exploration.
- Data validation with informative error responses.
- Pagination and sorting for endpoint data.

### Endpoints
#### Authentication Endpoints
- Base URL: /auth
    - POST /login or /signin - User login (returns JWT token).
    - POST /register or /signup - User registration (sends activation email).
    - GET /activate-account - Activates a user account using a token.


#### Article Management
- Base URL: /articles
    - GET / - Retrieve a paginated list of articles (supports sorting).
    - GET /{articleId} - Retrieve an article by ID.
    - POST / - Create a new article (requires ADMIN or EDITOR role).
    - PUT /{articleId} - Update an article by ID (requires ADMIN or EDITOR role).
    - DELETE /{articleId} - Delete an article by ID (requires ADMIN or EDITOR role).


#### Category Management
- Base URL: /categories
    - GET / - Retrieve a paginated list of categories (supports sorting).
    - GET /{categoryId} - Retrieve a category by ID.
    - POST / - Create a new category (requires ADMIN role).
    - PUT /{categoryId} - Update a category by ID (requires ADMIN role).
    - DELETE /{categoryId} - Delete a category by ID (requires ADMIN role).

#### Comment Management
- Base URL: /comments
    - GET / - Retrieve a paginated list of comments (supports sorting).
    - GET /{commentId} - Retrieve a comment by ID.
    - POST / - Create a new comment (authenticated user).
    - PUT /{commentId} - Update a comment by ID (requires ADMIN or EDITOR role).
    - DELETE /{commentId} - Delete a comment by ID (requires ADMIN or EDITOR role).


#### Customer Management
- Base URL: /customers
    - GET / - Retrieve a paginated list of customers (requires ADMIN role).
    - GET /{customerId} - Retrieve a customer by ID (requires ADMIN role).
    - PUT /{customerId} - Update a customer by ID (requires ADMIN role).
    - DELETE /{customerId} - Delete a customer by ID (requires ADMIN role).

### Prerequisites

- Java 11 or later installed
- Maven 3.x installed
- MySQL database 

### Setup
1) Clone the repository
    ```shell
      git clone https://github.com/m4rcingsxr/blogify.git
      cd blogify
    ```
2) Configure application.properties
3) Build and run application
     ```shell
      mvn clean package
      java -jar target/blog-app-1.1.jar
    ```

