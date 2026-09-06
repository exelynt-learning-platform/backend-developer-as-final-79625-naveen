# backend-developer-as-final-79625-naveen
Final Project Assignment - This repository contains the complete final project code and documentation.


# Resource Booking System

A production-style RESTful Resource Booking System built with Java 17, Spring Boot, Spring Security (JWT), and PostgreSQL.

## Features
- JWT Authentication (Stateless).
- Role-based Access Control (USER, ADMIN).
- Users can view and book resources (meeting rooms, laptops).
- Robust conflict checking (no overlapping reservations for a resource).
- Strict ownership verification (Users can only read/cancel their own reservations; Admins can manage all).
- Full CRUD for Resources and Reservations.
- Filtering, Pagination, and Sorting built-in for querying reservations.
- Global Exception handling with standardized API errors.
- Automated API Documentation with Swagger UI / OpenAPI 3.

## Tech Stack
- **Java 17**
- **Spring Boot 3** (Web, Data JPA, Security, Validation)
- **PostgreSQL**
- **JWT** (io.jsonwebtoken)
- **JUnit 5 / MockMvc** for Integration testing
- **Swagger / OpenAPI**

## Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+

## PostgreSQL Installation & Setup
1. Install PostgreSQL on your local machine.
2. Run the following command in psql or pgAdmin:
```sql
CREATE DATABASE booking_db;
```
3. Set your environment variables (do not commit secrets to Git).

### Environment Variables
Configure these in your system environment before running:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/booking_db
export DB_USERNAME=postgres
export DB_PASSWORD=Naveen@9989
export JWT_SECRET=replace-with-a-long-random-secret-at-least-32-characters
export JWT_ISSUER=resource-booking-api
export JWT_EXPIRATION_MINUTES=60
export SERVER_PORT=8080
```

## How to Run
```bash
# Run the test suite
mvn clean test

# Run the application
mvn spring-boot:run
```

## Seed Credentials
Upon startup, two default accounts are provisioned:
- **Admin**: `admin` / `Admin@123`
- **User**: `user` / `User@123`

## Testing Instructions
Tests use an isolated H2 in-memory database to prevent accidental destruction of PostgreSQL data. You can run `mvn test` to execute them.

## Swagger / OpenAPI
Once the app is running, navigate to:
- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- You can authorize via the "Authorize" button using a generated JWT token from the `/auth/login` endpoint.

## Postman Collection
A `postman_collection.json` file is located at the root of the project. Import this file into Postman and set the `baseUrl` variable to `http://localhost:8080`.

## Production Recommendations
- **Migrations**: In production, `ddl-auto: update` must be disabled. Use Flyway or Liquibase for robust schema migrations.
- **Passwords**: The JWT Secret must be injected securely via a vault (e.g. AWS Secrets Manager or HashiCorp Vault), never stored locally or in source control.

