# Booking System API

A comprehensive Java Spring Boot API for mobile application booking systems with country-specific packages, class scheduling, and concurrent booking management.

## 🚀 Features

### User Module
- User registration with email verification
- JWT-based authentication and authorization
- Profile management (view, change password, reset password)
- Account status management

### Package Module
- Country-specific package management
- Credit-based system with expiration dates
- Package purchase with payment integration
- User package tracking with remaining credits

### Booking Module
- Class schedule management by country
- Real-time booking with capacity management
- Waitlist functionality with FIFO promotion
- Concurrent booking prevention using Redis
- Time conflict detection
- Check-in system
- Cancellation with conditional credit refund

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Database**: MySQL 8.0 with JPA/Hibernate
- **Cache**: Redis for concurrency control and performance
- **Security**: Spring Security with JWT authentication
- **Scheduler**: Quartz for automated tasks
- **Documentation**: Swagger/OpenAPI 3
- **Containerization**: Docker & Docker Compose
- **Code Generation**: Lombok for reducing boilerplate

## 🏗 Architecture

The application follows a clean architecture pattern with:
- **Controllers**: RESTful API endpoints
- **Services**: Business logic layer
- **Repositories**: Data access layer
- **Entities**: JPA entity models with Lombok annotations
- **DTOs**: Data transfer objects with Lombok
- **Security**: JWT-based authentication
- **Scheduling**: Automated background tasks

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- Postman (for API testing)

## 🚀 Quick Start

### 1. Clone and Build
```bash
git clone <repository-url>
cd booking-system-api
mvn clean package -DskipTests
```

### 2. Start Services with Docker Compose
```bash
docker-compose up -d
```

This will start:
- **MySQL database** on port 3306
- **Redis cache** on port 6379
- **Spring Boot application** on port 8080

### 3. Verify Installation
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 📖 API Documentation

### Swagger UI
Access the interactive API documentation at: **http://localhost:8080/swagger-ui.html**

### Postman Collection
Import the provided Postman collection for comprehensive API testing:

1. **Collection File**: `postman/Booking-System-API.postman_collection.json`
2. **Environment File**: `postman/Booking-System-Environment.postman_environment.json`

#### How to Import:
1. Open Postman
2. Click **Import** button
3. Select **File** tab
4. Choose both JSON files from the `postman/` directory
5. Click **Import**

#### Collection Features:
- ✅ **Pre-configured requests** for all endpoints
- ✅ **Automatic JWT token management**
- ✅ **Environment variables** for easy configuration
- ✅ **Sample request bodies** with realistic data
- ✅ **Test scripts** for token extraction

### Authentication Flow
1. **Register**: `POST /api/auth/register`
2. **Verify Email**: `GET /api/auth/verify-email?token={token}`
3. **Login**: `POST /api/auth/login` (JWT token auto-saved in Postman)
4. **Use Token**: Automatically included in subsequent requests

## 🔗 Key API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |
| GET | `/api/auth/verify-email` | Verify email address |
| POST | `/api/auth/reset-password` | Reset password |

### User Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/profile` | Get user profile |
| POST | `/api/users/change-password` | Change password |

### Package Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/packages?country={country}` | Get available packages |
| GET | `/api/packages/my-packages` | Get user's packages |
| POST | `/api/packages/purchase` | Purchase package |

### Booking Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/schedules?country={country}` | Get class schedules |
| POST | `/api/bookings/{classScheduleId}` | Book a class |
| DELETE | `/api/bookings/{bookingId}` | Cancel booking |
| POST | `/api/bookings/{bookingId}/checkin` | Check in to class |
| GET | `/api/bookings` | Get user's bookings |

## 💼 Business Logic

### Package System
- **Country-Specific**: Packages are tied to specific countries (SG, MY, etc.)
- **Credit-Based**: Each package contains credits for booking classes
- **Expiration**: Packages have validity periods and expiration dates
- **Auto-Expiry**: Expired packages are automatically marked as expired

### Booking System
- **Capacity Management**: Classes have maximum capacity limits
- **Waitlist System**: FIFO waitlist when classes are full
- **Concurrency Control**: Redis locks prevent overbooking
- **Time Conflict Detection**: Users cannot book overlapping classes
- **Cancellation Policy**: Credits refunded if cancelled 4+ hours before class
- **Check-in Window**: 15 minutes before to 30 minutes after class start

### Automated Tasks
- **Class Completion**: Process finished classes and refund waitlist credits
- **Package Expiry**: Daily update of expired packages
- **Waitlist Promotion**: Auto-promote waitlisted users when spots open

## 🗄 Database Schema

### Core Entities
- **Users**: User accounts with verification status
- **Packages**: Available packages by country
- **UserPackages**: User's purchased packages with remaining credits
- **ClassSchedules**: Available classes by country and time
- **ClassBookings**: User bookings with status tracking

### Sample Data
The system comes pre-loaded with:
- **6 packages** (3 for Singapore, 3 for Malaysia)
- **10 class schedules** (5 per country)
- **Realistic pricing** and credit requirements

## 🔒 Security Features

- **JWT Authentication**: Stateless token-based authentication
- **Password Encryption**: BCrypt hashing
- **Email Verification**: Required for account activation
- **Input Validation**: Comprehensive request validation
- **CORS Configuration**: Properly configured for web clients

## 🧪 Testing with Postman

### Quick Test Flow:
1. **Import Collection & Environment**
2. **Register User**: Use "Register User" request
3. **Login**: Use "Login User" request (JWT auto-saved)
4. **Get Packages**: Check available packages for SG/MY
5. **Purchase Package**: Buy a package with mock payment
6. **View Schedules**: See available classes
7. **Book Class**: Make a booking
8. **Check Bookings**: View your bookings

### Environment Variables:
- `base_url`: http://localhost:8080
- `jwt_token`: Auto-populated after login
- `package_id`: Sample package ID (1)
- `class_schedule_id`: Sample class ID (1)
- `booking_id`: Sample booking ID (1)

## 🐳 Docker Configuration

### Services:
- **MySQL 8.0**: Database with persistent volume
- **Redis 7**: Cache and session storage
- **Spring Boot App**: Main application

### Ports:
- **8080**: Spring Boot application
- **3306**: MySQL database
- **6379**: Redis cache

## 📊 Monitoring & Logging

### Application Logs:
```bash
docker-compose logs -f app
```

### Database Logs:
```bash
docker-compose logs -f mysql
```

### Redis Logs:
```bash
docker-compose logs -f redis
```

## 🔧 Configuration

### Key Configuration Files:
- `application.yml`: Main application configuration
- `docker-compose.yml`: Container orchestration
- `init.sql`: Database initialization with sample data

### Environment Variables:
- Database credentials
- Redis configuration
- JWT secret and expiration
- Email service settings

## 🚀 Production Deployment

### Considerations:
- Environment-specific configuration
- Database connection pooling
- Redis clustering for high availability
- Load balancing for horizontal scaling
- SSL/TLS certificate configuration
- Monitoring and alerting setup
- Backup and disaster recovery

### Build for Production:
```bash
mvn clean package -Pprod
docker build -t booking-system-api .
```

## 📝 Sample API Usage

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890"
  }'
```

### 2. Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

### 3. Get Available Packages
```bash
curl -X GET "http://localhost:8080/api/packages?country=SG"
```

### 4. Purchase Package (with JWT)
```bash
curl -X POST http://localhost:8080/api/packages/purchase \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "packageId": 1,
    "cardId": "card_123456789"
  }'
```

### 5. Book a Class
```bash
curl -X POST http://localhost:8080/api/bookings/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the Swagger documentation
- Review the Postman collection examples

---

**Happy Coding! 🎉**