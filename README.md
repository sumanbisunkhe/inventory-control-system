<div align="center">

# 🏢  Inventory Control System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![JWT](https://img.shields.io/badge/JWT-Security-red.svg)](https://jwt.io/)

> *Your Complete Solution for Smart Inventory Management*

[🎯 Overview](#-overview) •
[🔄 Workflow](#-operational-workflow) •
[🌟 Features](#-features) •
[💻 Tech Stack](#-tech-stack) •
[📦 Installation](#-installation) •
[📖 Documentation](#-documentation) •
[👨‍💻 Author](#-author)

</div>


## 🎯 Overview


The Inventory Control System is a dependable and efficient solution designed to simplify inventory management. It offers a range of features, including real-time tracking of products and orders, ensuring seamless and effective inventory operations.

The process begins with the **OWNER** registering their inventory in the system. Upon successful registration, the owner receives a welcome email confirming their registration. Once registered, the owner can add **suppliers** to the system.
Next, **products** are added to the inventory and linked to their respective suppliers. If the quantity of any product falls to or below the minimum stock level, the system automatically notifies the owner via email about the issue.
To replenish stock, the owner can place an order with the relevant supplier directly through the system. Once an order request is made, the supplier is notified via email and fulfills the request according to the owner’s requirements.

This streamlined process ensures that inventory is managed efficiently, minimizing stockouts and facilitating smooth operations for the owner. The Inventory Control System is a comprehensive tool for effective inventory management.

## 🔄 Operational Workflow
- [Owner Registration](#-1-registration)
- [Supplier Management](#-2-supplier-management)
- [CSV Operations](#-3-csv-operations)
- [Product Management](#-4-product-management)
- [Smart Notifications](#-5-smart-notifications)
- [Order Processing](#-6-order-processing)

### 📝️ 1. Registration
- 🖊 **Business Owner Registration**
  - Quick and easy system registration
  - Welcome email confirmation
  - Secure account setup

### 🤝 2. Supplier Management
- 👥 **Multi-Supplier Integration**
  - Seamless supplier onboarding
  - Comprehensive contact database
  - Automated supplier communications
  - Smart product-supplier mapping

### 📊 3. CSV Operations
- 📥 **Data Import/Export**
  - Bulk product upload
  - Supplier data import
  - Inventory export
  - Template-based processing

### 📦 4. Product Management
- 🏷️ **Inventory Control**
  - Intuitive product addition
  - Supplier linkage system
  - Real-time quantity tracking
  - Supplier-based categorization

### ⚡ 5. Smart Notifications
- 🔔 **Automated Alerts**
  - Continuous stock monitoring
  - Low-stock threshold alerts
  - Instant email notifications
  - Supplier update system

### 📋 6. Order Processing
- 🔄 **Streamlined Ordering**
  - One-click order placement
  - Intelligent supplier routing
  - Automated order notifications
  - Customized delivery management

## 🌟 Features

<table>
<tr>
<td>

### 🔐 Security & Access
- JWT-based authentication
- Role-based access control
- Secure API endpoints
- Structured error handling

</td>
<td>

### 📦 Core Functions
- Complete product CRUD operations
- Real-time inventory tracking
- Order lifecycle management
- Supplier relationship management

</td>
</tr>
<tr>
<td>

### 🔄 Data Management
- Bulk CSV import/export
- Real-time notifications
- Data validation
- Automated testing suite

</td>
<td>

### ⚡ Development
- Hot reload support
- Comprehensive logging
- Error tracking
- DevOps-ready configuration

</td>
</tr>
</table>


## 💻 Tech Stack

### Core Framework & Security
```mermaid
graph LR
    A[Spring Boot] --> B[Spring Security]
    B --> C[JWT Authentication]
    A --> D[Spring Data JPA]
```

### Key Components
- **Database**: MySQL / PostgreSQL
- **Authentication**: JWT with Spring Security
- **Documentation**: Postman
- **Testing**: JUnit, Mockito
- **Utilities**: Lombok, ModelMapper
- **Validation**: Jakarta Validation API

## 📦 Installation

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL

### 🚀 Getting Started
1. **Clone Repository**
```bash
git clone https://github.com/sumanbisunkhe/inventory-control-system.git
cd inventory-control-system
```

2. **Configure Database**
   Create `application.properties`:
 ```properties
     spring.application.name=inventory-control-system
            
     # MySQL Database Configuration
     spring.datasource.url=jdbc:mysql://localhost:3306/inventory_control_system_db
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
            
     # Hibernate JPA Configuration
     spring.jpa.hibernate.ddl-auto=update
     spring.jpa.show-sql=true
     spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
            
     # Other Spring Boot Settings
     server.port=8080
     spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false
            
            
     # ========== JWT Configuration ==========
     jwt.secret=0oaP4MrLeRXkmISd9HXo6Eth90udQMvCICrKbaAfqbE=
            
     # ========== Logging Configuration ==========
     logging.level.org.hibernate.SQL=DEBUG
     logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
            
     # ====== Mail Configuration ======
     spring.mail.host=smtp.gmail.com
     spring.mail.port=587
     spring.mail.username=you_email
     spring.mail.password=your_email_app_password
     spring.mail.properties.mail.smtp.auth=true
     spring.mail.properties.mail.smtp.starttls.enable=true
   ```

3. **Build & Run**
```bash
mvn clean install
mvn spring-boot:run
```

## 🗂️ Project Structure

```
inventory-management-system/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── inventory/
│   │   │               ├── config/
│   │   │               │   └── SecurityConfig.java
│   │   │               │ 
│   │   │               ├── controller/
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── CsvController.java
│   │   │               │   ├── OrderController.java
│   │   │               │   ├── ProductController.java
│   │   │               │   ├── SupplierController.java
│   │   │               │   └── UserController.java
│   │   │               │
│   │   │               ├── dto/
│   │   │               │   ├── OrderDto.java
│   │   │               │   ├── ProductDto.java
│   │   │               │   ├── SupplierDto.java
│   │   │               │   └── UserDto.java
│   │   │               │
│   │   │               ├── enums/
│   │   │               │   ├── Category.java
│   │   │               │   ├── OrderStatus.java
│   │   │               │   ├── RoleName.java
│   │   │               │   └── Status.java
│   │   │               │
│   │   │               ├── exceptions/
│   │   │               │   ├── CsvlmportException.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── JwtTokenException.java
│   │   │               │   ├── OrderNotFoundException.java
│   │   │               │   ├── ProductNotFoundException.java
│   │   │               │   └── SupplierNotFoundException.java
│   │   │               │
│   │   │               ├── model/
│   │   │               │   ├── Order.java
│   │   │               │   ├── Product.java
│   │   │               │   ├── Role.java
│   │   │               │   ├── Supplier.java
│   │   │               │   └── User.java
│   │   │               │
│   │   │               ├── repo/
│   │   │               │   ├── OrderRepo.java
│   │   │               │   ├── ProductRepo.java
│   │   │               │   ├── RoleRepo.java
│   │   │               │   ├── SupplierRepo.java
│   │   │               │   └── UserRepo.java
│   │   │               │
│   │   │               ├── security/
│   │   │               │   ├── AuthenticationRequest.java
│   │   │               │   ├── AuthenticationResponse.java
│   │   │               │   ├── JwtRequestFilter.java
│   │   │               │   └── JwtUtil.java
│   │   │               │
│   │   │               │
│   │   │               ├── service/
│   │   │               │   ├── impl/
│   │   │               │   │   ├── EmailServiceImpl.java
│   │   │               │   │   ├── OrderServiceImpl.java
│   │   │               │   │   ├── ProductServiceImpl.java
│   │   │               │   │   ├── SupplierServiceImpl.java
│   │   │               │   │   └── UserServiceImpl.java
│   │   │               │   │
│   │   │               │   ├── EmailService.java
│   │   │               │   ├── OrderService.java
│   │   │               │   ├── ProductService.java
│   │   │               │   ├── SupplierService.java
│   │   │               │   └── UserService.java
│   │   │               │   
│   │   │               ├── utils/
│   │   │               │   ├── CsvUtils.java
│   │   │               │   ├── CustomCustomerDetailsService.java
│   │   │               │   ├── CustomEmailMessage.java
│   │   │               │   ├── Databaselnitializer.java
│   │   │               │   ├── KeyGenerator.java
│   │   │               │   └── Rolelnitializer
│   │   │               │ 
│   │   │               └── InventoryManagementSystemApplication.java
│   │   └── resources/
│   │       ├── docs/
│   │       │   └── Inventory Control System API Documentation.pdf
│   │       └── application.properties
│   │       
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── inventory/
│                       ├── controller/
│                       │   ├── AuthControllerTest.java
│                       │   ├── CsvControllerTest.java
│                       │   ├── OrderControllerTest.java
│                       │   ├── ProductControllerTest.java
│                       │   ├── SupplierControllerTest.java
│                       │   └── UserControllerTest.java
│                       │ 
│                       ├── service/
│                       │   ├── EmailServicelmplTest.java
│                       │   ├── OrderServicelmplTest.java
│                       │   ├── ProductServicelmplTest.java
│                       │   ├── SupplierServicelmplTest.java
│                       │   └── UserServicelmplTest.java   
│                       │
│                       └── InventoryManagementSystemApplicationTests.java
│   
│   
├── LICENSE  
├── mvnw  
├── mvnw.cmd  
├── pom.xml
└── README.md
```

## 🔧 Core Dependencies

<table>
<tr><th>Category</th><th>Dependencies</th></tr>
<tr>
<td>Core</td>
<td>

- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security

</td>
</tr>
<tr>
<td>Security</td>
<td>

- jjwt-api
- jjwt-impl
- jjwt-jackson

</td>
</tr>
<tr>
<td>Database</td>
<td>

- mysql-connector-j
- postgresql

</td>
</tr>
<tr>
<td>Utilities</td>
<td>

- lombok
- modelmapper
- commons-csv

</td>
</tr>
</table>

## 📖 Documentation


### 📚 Available Documentation Formats

| Format                                          | Description                                         | Link                                                                                |
|-------------------------------------------------|-----------------------------------------------------|-------------------------------------------------------------------------------------|
| ![PDF](src/main/resources/docs/pdf.png)         | Comprehensive system architecture and API reference | [View PDF](src/main/resources/docs/inventory-control-system-api-documentation.pdf)  |
| ![Postman](src/main/resources/docs/postman.png) | Interactive API documentation with examples         | [View in Postman](https://documenter.getpostman.com/view/32179535/2sAYBSkYmW#intro) |



## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👨‍💻 Author


<div align="center">
  <img src="src/main/resources/docs/prof.jpeg" style="width:100px; height:100px;border-radius:50%; " alt="Author Avatar">

### Suman Bisunkhe
Java Developer

[![GitHub](https://img.shields.io/badge/GitHub-sumanbisunkhe-black?style=flat&logo=github)](https://github.com/sumanbisunkhe)
[![Email](https://img.shields.io/badge/Email-sumanbisunkhe304%40gmail.com-red?style=flat&logo=gmail)](mailto:sumanbisunkhe304@gmail.com)
</div>

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ by [Suman Bisunkhe](https://github.com/sumanbisunkhe)

</div>