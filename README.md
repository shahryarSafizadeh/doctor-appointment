# Doctor Appointment System

## Overview
The Doctor Appointment System is a Spring Boot application designed to facilitate appointment scheduling between doctors and patients. The application provides functionalities for user registration, login, viewing and reserving appointments, and more, with security features implemented using JWT (JSON Web Tokens).

## Features
- **User Registration and Login**: Users can register as doctors or patients and log in to the system.
- **JWT Authentication and Authorization**: Secure authentication and authorization using JWT.
- **Appointment Management**: Doctors can set open appointment times, view all appointments, and delete appointments. Patients can view all open appointments and reserve them.
- **Concurrency Handling**: Ensures appointment data integrity with proper concurrency checks.
- **Role-Based Access Control**: Doctors and patients have access to different sets of functionalities based on their roles.

## Technologies Used
- **Spring Boot**: Application framework.
- **Spring Security**: For authentication and authorization.
- **JWT**: For secure token-based authentication.
- **Hibernate**: ORM for database interactions.
- **H2 Database**: In-memory database for development and testing.
- **Maven**: Build and dependency management tool.
