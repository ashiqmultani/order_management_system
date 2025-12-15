# Order Management System – Spring Boot

## 📌 Project Description
This project is a **basic Order Management System** developed using **Java Spring Boot**.  
It provides RESTful APIs to create and manage orders, maintain order lifecycle statuses, and
retrieve order information for users.

The application follows a layered architecture (Controller, Service, Repository) and persists
data using Spring Data JPA. Order states are strictly controlled using predefined status values
to ensure data consistency and reliability.

---

## 🧩 Functional Requirements

The system supports:
- Order creation
- Order status management
- Order retrieval

Users can:
- Create a new order
- View order details
- View all orders for a user

Order lifecycle statuses:
- CREATED
- PROCESSING
- COMPLETED
- CANCELLED

---

## 🛠️ Tech Stack

- Java 17
- Spring Boot
- Spring Web (REST APIs)
- Spring Data JPA
- Hibernate
- MySQL / H2 Database
- Maven
- Lombok

---

## 🏗️ Architecture Overview

The application follows a standard layered architecture:

