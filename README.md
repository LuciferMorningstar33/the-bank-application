# the-bank-app
# Spring Boot Banking Application

[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)  [![forthebadge](https://forthebadge.com/images/badges/built-with-love.svg)](https://forthebadge.com)

A simple banking application built with Spring Boot that allows users to perform basic banking operations such as creating accounts, making deposits, withdrawals, and viewing account balances.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Database Setup](#database-setup)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Features
- User registration and authentication
- Account creation and management
- Deposit and withdrawal functionality
- View account balance and transaction history
- RESTful API for all banking operations
- Email service for sending banking statements in PDF format

## Technologies Used
- **Java**: Programming language
- **Spring Boot**: Framework for building the application
- **Spring Security**: For authentication and authorization
- **Hibernate**: ORM for database interaction
- **MySQL**: Database for storing user and account information
- **Maven**: Dependency management
- **JUnit**: Testing framework

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/LuciferMorningstar33/the-bank-application.git
   ```
2. Navigate to the project directory and run:
   ```bash
   mvn clean install
   ```

## Usage
- Start the application by running the main class in your IDE or using Maven:
  ```bash
  mvn spring-boot:run
  ```

## API Endpoints
- **POST** `/api/users/register`: Register a new user
- **POST** `/api/users/login`: User login
- **GET** `/api/accounts/{id}`: Get account details
- **POST** `/api/accounts/{id}/deposit`: Deposit money
- **POST** `/api/accounts/{id}/withdraw`: Withdraw money
- **GET** `/api/transactions`: Get transaction history

## Database Setup
- Ensure MySQL is installed and running.
- Create a database named `the-bank`.
- Update the `application.properties` file with your database credentials.

## Testing
- Unit tests can be run using JUnit.
- To run all tests, use:
  ```bash
  mvn test
  ```

## Contributing
- Contributions are welcome! Please fork the repository and submit a pull request.

## License
- This project is licensed under the MIT License. See the LICENSE file for details.

## Contact
- For inquiries, please contact [LuciferMorningstar33](https://github.com/LuciferMorningstar33).

## Folder Structure
- **controllers**: Contains `TransactionController` and `UserController`
- **dto**: Contains `BankResponse`, `EmailDetails`, and `Transaction`
- **entity**: Contains `Transaction` and `User`
- **repository**: Contains JPA repositories `TransactionRepository` and `UserRepository`
- **service**: Contains `BankStatementGeneration`, `EmailSender`, and `UserService`
- **utils**: Contains `AccountUtils` and `EmailUtils`

The application also includes a JavaMailSender service to send PDF statements of banking transactions to users.