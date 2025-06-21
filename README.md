# Booking System API

A comprehensive Java Spring Boot API for mobile application booking systems with country-specific packages, class scheduling, and concurrent booking management.

## Features

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

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Database**: MySQL 8.0 with JPA/Hibernate
- **Cache**: Redis for concurrency control and performance
- **Security**: Spring Security with JWT authentication
- **Scheduler**: Quartz for automated tasks
- **Documentation**: Swagger/OpenAPI 3
- **Containerization**: Docker & Docker Compose

## Architecture

The application follows a clean architecture pattern with:
- **Controllers**: RESTful API endpoints
- **Services**: Business logic layer
- **Repositories**: Data access layer
- **Entities**: JPA entity models
- **DTOs**: Data transfer objects
- **Security**: JWT-based authentication
- **Scheduling**: Automated background tasks

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker & Docker Compose

### Running with Docker Compose

1. **Clone and build the application**:
```bash
mvn clean package -DskipTests
```

2. **Start all services**:
```bash
docker-compose up -d
```

This will start:
- MySQL database on port 3306
- Redis cache on port 6379
- Spring Boot application on port 8080

### Manual Setup

1. **Database Setup**:
```bash
# Start MySQL
docker run -d --name mysql-booking \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -e MYSQL_DATABASE=booking_system \
  -e MYSQL_USER=booking_user \
  -e MYSQL_PASSWORD=booking_password \
  -p 3306:3306 mysql:8.0

# Start Redis
docker run -d --name redis-booking \
  -p 6379:6379 redis:7-alpine \
  redis-server --requirepass redis_password
```

2. **Run Application**:
```bash
mvn spring-boot:run
```

## API Documentation

### Swagger UI
Access the interactive API documentation at: `http://localhost:8080/swagger-ui.html`

### Authentication
1. Register a new user: `POST /api/auth/register`
2. Verify email: `GET /api/auth/verify-email?token={token}`
3. Login: `POST /api/auth/login`
4. Use the JWT token in the Authorization header: `Bearer {token}`

### Key Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/verify-email` - Verify email
- `POST /api/auth/reset-password` - Reset password

#### User Management
- `GET /api/users/profile` - Get user profile
- `POST /api/users/change-password` - Change password

#### Package Management
- `GET /api/packages?country={country}` - Get available packages
- `GET /api/packages/my-packages` - Get user's packages
- `POST /api/packages/purchase` - Purchase package

#### Booking Management
- `GET /api/schedules?country={country}` - Get class schedules
- `POST /api/bookings/{classScheduleId}` - Book a class
- `DELETE /api/bookings/{bookingId}` - Cancel booking
- `POST /api/bookings/{bookingId}/checkin` - Check in to class
- `GET /api/bookings` - Get user's bookings

## Business Logic

### Package System
- Packages are country-specific (e.g., Singapore packages can only book Singapore classes)
- Each package has credits, price, validity period, and expiration date
- Credits are deducted upon successful booking
- Expired packages are automatically marked as expired

### Booking System
- **Capacity Management**: Classes have maximum capacity limits
- **Waitlist**: When full, users are added to a waitlist with FIFO ordering
- **Concurrent Booking**: Redis locks prevent overbooking during simultaneous requests
- **Time Conflicts**: Users cannot book overlapping classes
- **Cancellation Policy**: Credits refunded if cancelled 4+ hours before class
- **Check-in Window**: 15 minutes before to 30 minutes after class start

### Automated Tasks
- **Completed Classes**: Process finished classes and refund waitlist credits
- **Package Expiry**: Update expired packages daily
- **Waitlist Promotion**: Automatically promote waitlisted users when spots open

## Database Schema

Key entities:
- **Users**: User accounts with verification status
- **Packages**: Available packages by country
- **UserPackages**: User's purchased packages with remaining credits
- **ClassSchedules**: Available classes by country and time
- **ClassBookings**: User bookings with status tracking

## Security Features

- JWT-based stateless authentication
- Password encryption with BCrypt
- Email verification for new accounts
- Role-based access control ready for extension
- Input validation and sanitization
- Rate limiting via Redis (configurable)

## Testing

Run the test suite:
```bash
mvn test
```

## Production Considerations

- Environment-specific configuration
- Database connection pooling
- Redis clustering for high availability
- Load balancing for horizontal scaling
- Monitoring and logging setup
- SSL/TLS certificate configuration
- Backup and disaster recovery planning

## Sample API Usage

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

### 2. Login
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

### 4. Purchase Package
```bash
curl -X POST http://localhost:8080/api/packages/purchase \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "packageId": 1,
    "cardId": "card_123"
  }'
```

### 5. Book a Class
```bash
curl -X POST http://localhost:8080/api/bookings/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```