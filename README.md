# Airbnb Clone - Vacation Rental Platform

A comprehensive vacation rental platform built with Spring Boot, featuring property listings, booking management, payment processing with PayPal, and a complete review system.

## 🚀 Features

### Core Features
- **Property Management**: Create, update, and manage vacation rental properties
- **Booking System**: Complete booking workflow with availability management
- **Payment Processing**: PayPal integration for secure payments
- **Review System**: Guest and host reviews with ratings
- **User Management**: Multi-role user system (Guest, Host, Super Host, Admin)
- **Image Management**: AWS S3 integration for property images
- **Search & Filtering**: Advanced property search with multiple criteria

### Advanced Features
- **Multi-level Caching**: Caffeine (local) + Redis (distributed) caching
- **Auditing**: Hibernate Envers for data change tracking
- **Scheduling**: Spring Task for automated maintenance tasks
- **Internationalization**: Multi-language support (English, French)
- **GraphQL API**: Flexible API with GraphQL support
- **Security**: JWT-based authentication and authorization
- **Real-time Updates**: WebSocket support for live notifications

## 🛠️ Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data persistence
- **Spring Security** - Security framework
- **Spring Cache** - Caching abstraction
- **Spring GraphQL** - GraphQL support
- **Hibernate Envers** - Data auditing
- **H2 Database** - Development database
- **PostgreSQL** - Production database
- **Redis** - Distributed caching
- **Caffeine** - Local caching

### Payment & External Services
- **PayPal SDK** - Payment processing
- **AWS S3** - Image storage
- **Spring Mail** - Email notifications

### Development Tools
- **Maven** - Build tool
- **Docker** - Containerization
- **JUnit 5** - Testing framework
- **Testcontainers** - Integration testing

## 📁 Project Structure

```
airbnb-clone/
├── src/
│   ├── main/
│   │   ├── java/com/enterprise/airbnb/
│   │   │   ├── AirbnbApplication.java
│   │   │   ├── config/           # Configuration classes
│   │   │   │   ├── CachingConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── PayPalConfig.java
│   │   │   │   ├── GraphQLConfig.java
│   │   │   │   └── I18nConfig.java
│   │   │   ├── controller/       # REST Controllers
│   │   │   │   ├── PropertyController.java
│   │   │   │   ├── BookingController.java
│   │   │   │   ├── HostController.java
│   │   │   │   ├── ReviewController.java
│   │   │   │   ├── ImageUploadController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   └── PayPalWebhookController.java
│   │   │   ├── graphql/          # GraphQL DTOs and Resolvers
│   │   │   │   ├── dto/          # GraphQL Data Transfer Objects
│   │   │   │   │   ├── BookingInput.java
│   │   │   │   │   ├── BookingResponse.java
│   │   │   │   │   ├── PropertyInput.java
│   │   │   │   │   ├── PropertySearchInput.java
│   │   │   │   │   ├── PropertySearchResponse.java
│   │   │   │   │   ├── ReviewInput.java
│   │   │   │   │   ├── PaymentInput.java
│   │   │   │   │   ├── PaymentResponse.java
│   │   │   │   │   ├── UserInput.java
│   │   │   │   │   └── AuthResponse.java
│   │   │   │   ├── resolver/     # GraphQL Resolvers
│   │   │   │   │   ├── PropertyResolver.java
│   │   │   │   │   ├── BookingResolver.java
│   │   │   │   │   ├── UserResolver.java
│   │   │   │   │   ├── PaymentResolver.java
│   │   │   │   │   ├── ReviewResolver.java
│   │   │   │   │   ├── QueryResolver.java
│   │   │   │   │   └── MutationResolver.java
│   │   │   │   └── schema.graphqls
│   │   │   ├── model/            # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Property.java
│   │   │   │   ├── Booking.java
│   │   │   │   ├── Review.java
│   │   │   │   ├── Payment.java
│   │   │   │   ├── Image.java
│   │   │   │   └── enums/
│   │   │   ├── repository/       # JPA Repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── PropertyRepository.java
│   │   │   │   ├── BookingRepository.java
│   │   │   │   └── ReviewRepository.java
│   │   │   ├── service/          # Business Logic
│   │   │   │   ├── UserService.java
│   │   │   │   ├── PropertyService.java
│   │   │   │   ├── BookingService.java
│   │   │   │   ├── PaymentService.java
│   │   │   │   ├── ImageService.java
│   │   │   │   ├── HostService.java
│   │   │   │   ├── ReviewService.java
│   │   │   │   ├── CurrencyService.java
│   │   │   │   ├── PropertyAvailabilityService.java
│   │   │   │   └── PayPalWebhookService.java
│   │   │   ├── util/             # Utility Classes
│   │   │   │   ├── JwtRequestFilter.java
│   │   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │   ├── JwtUtil.java
│   │   │   │   └── DateUtils.java
│   │   │   ├── factory/          # Factory Pattern
│   │   │   │   └── BookingFactory.java
│   │   │   ├── scheduler/        # Scheduled Tasks
│   │   │   │   └── ListingCleanupScheduler.java
│   │   │   ├── event/            # Event Handling
│   │   │   │   ├── BookingCreatedEvent.java
│   │   │   │   └── BookingEventListener.java
│   │   │   ├── audit/            # Auditing
│   │   │   │   ├── AuditTrail.java
│   │   │   │   ├── AuditTrailListener.java
│   │   │   │   └── HibernateEnversConfig.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── messages.properties
│   │       ├── messages_fr.properties
│   │       └── schema.graphqls
│   └── test/java/com/enterprise/airbnb/
│       ├── service/              # Service Layer Tests
│       │   ├── UserServiceTest.java
│       │   ├── BookingServiceTest.java
│       │   ├── PropertyServiceTest.java
│       │   ├── PaymentServiceTest.java
│       │   └── ReviewServiceTest.java
│       ├── controller/           # Controller Layer Tests
│       │   ├── PropertyControllerTest.java
│       │   ├── BookingControllerTest.java
│       │   ├── AuthControllerTest.java
│       │   └── ReviewControllerTest.java
│       ├── integration/          # Integration Tests
│       │   ├── PropertyIntegrationTest.java
│       │   ├── BookingIntegrationTest.java
│       │   └── UserIntegrationTest.java
│       └── AirbnbApplicationTests.java
├── .gitignore
├── .env.example
├── docker-compose.yml
├── README.md
├── pom.xml
└── Dockerfile
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Redis (for caching)
- PostgreSQL (for production)
- AWS S3 account (for image storage)
- PayPal Developer account (for payments)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/airbnb-clone.git
   cd airbnb-clone
   ```

2. **Configure environment variables**
   ```bash
   # Create .env file
   cp .env.example .env
   
   # Edit .env with your configuration
   nano .env
   ```

3. **Set up the database**
   ```bash
   # For development (H2 - in-memory)
   # No setup required, H2 starts automatically
   
   # For production (PostgreSQL)
   createdb airbnb_clone
   ```

4. **Start Redis**
   ```bash
   # Using Docker
   docker run -d -p 6379:6379 redis:alpine
   
   # Or install locally
   redis-server
   ```

5. **Build and run the application**
   ```bash
   # Build the project
   mvn clean install
   
   # Run the application
   mvn spring-boot:run
   ```

6. **Access the application**
   - API: http://localhost:8080/api
   - GraphQL Playground: http://localhost:8080/graphiql
   - H2 Console: http://localhost:8080/h2-console
   - Actuator: http://localhost:8080/actuator

### Environment Variables

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/airbnb_clone
DATABASE_USERNAME=airbnb_user
DATABASE_PASSWORD=airbnb_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# PayPal
PAYPAL_CLIENT_ID=your_paypal_client_id
PAYPAL_CLIENT_SECRET=your_paypal_client_secret
PAYPAL_WEBHOOK_ID=your_paypal_webhook_id

# AWS S3
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key
AWS_S3_BUCKET=your_s3_bucket_name
AWS_REGION=us-east-1

# Email
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# JWT
JWT_SECRET=your_jwt_secret_key
```

## 📚 API Documentation

### REST API Endpoints

#### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `POST /api/auth/refresh` - Refresh JWT token

#### Properties
- `GET /api/properties` - Get all properties
- `GET /api/properties/{id}` - Get property by ID
- `POST /api/properties` - Create new property
- `PUT /api/properties/{id}` - Update property
- `DELETE /api/properties/{id}` - Delete property
- `GET /api/properties/search` - Search properties

#### Bookings
- `GET /api/bookings` - Get all bookings
- `GET /api/bookings/{id}` - Get booking by ID
- `POST /api/bookings` - Create new booking
- `PUT /api/bookings/{id}` - Update booking
- `DELETE /api/bookings/{id}` - Cancel booking

#### Payments
- `GET /api/payments` - Get all payments
- `GET /api/payments/{id}` - Get payment by ID
- `POST /api/payments` - Create payment
- `POST /api/payments/{id}/process` - Process payment
- `POST /api/payments/{id}/refund` - Refund payment

#### Reviews
- `GET /api/reviews` - Get all reviews
- `GET /api/reviews/{id}` - Get review by ID
- `POST /api/reviews` - Create review
- `PUT /api/reviews/{id}` - Update review
- `DELETE /api/reviews/{id}` - Delete review

### GraphQL API

Access the GraphQL Playground at `http://localhost:8080/graphiql`

#### Example Queries

```graphql
# Get properties with search
query SearchProperties($input: PropertySearchInput!) {
  searchProperties(input: $input) {
    properties {
      id
      title
      description
      pricePerNight
      rating
      images
      address {
        city
        country
      }
      host {
        firstName
        lastName
      }
    }
    totalCount
    page
    size
  }
}

# Create booking
mutation CreateBooking($input: BookingInput!) {
  createBooking(input: $input) {
    booking {
      id
      bookingReference
      totalPrice
      status
    }
    paymentUrl
    message
  }
}
```

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run integration tests
mvn test -Dtest=*IntegrationTest

# Run tests with coverage
mvn test jacoco:report
```

### Test Categories
- **Unit Tests**: Service layer and utility classes
- **Integration Tests**: Repository and controller layers
- **End-to-End Tests**: Complete API workflows
- **Performance Tests**: Load and stress testing

## 🚀 Deployment

### Docker Deployment

1. **Build Docker image**
   ```bash
   docker build -t airbnb-clone .
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

### Production Deployment

1. **Set production profile**
   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   ```

2. **Configure production database**
   ```bash
   # Update application-prod.yml with production database settings
   ```

3. **Deploy to cloud platform**
   ```bash
   # Example for AWS Elastic Beanstalk
   eb deploy
   ```

## 📊 Monitoring & Observability

### Health Checks
- **Health Endpoint**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`

### Logging
- **Application Logs**: `logs/airbnb-clone.log`
- **Log Levels**: Configurable per environment
- **Structured Logging**: JSON format for production

### Caching
- **Cache Statistics**: `/actuator/caches`
- **Cache Management**: Clear and refresh caches
- **Performance Monitoring**: Cache hit/miss ratios

## 🔧 Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
# Database
spring:
  datasource:
    url: jdbc:h2:mem:airbnb_clone
    username: sa
    password: password

# Cache
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=10000,expireAfterWrite=1h

# JWT
jwt:
  secret: mySecretKey
  expiration: 86400

# PayPal
paypal:
  client:
    id: ${PAYPAL_CLIENT_ID}
    secret: ${PAYPAL_CLIENT_SECRET}
  mode: sandbox
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding standards
- Write comprehensive tests
- Update documentation
- Use meaningful commit messages
- Follow the existing code structure

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- PayPal for payment processing capabilities
- AWS for cloud storage services
- The open-source community for various libraries and tools

## 📞 Support

For support and questions:
- Create an issue in the GitHub repository
- Contact the development team
- Check the documentation and FAQ

## 🗺️ Roadmap

### Upcoming Features
- [ ] Mobile app (React Native)
- [ ] Advanced analytics dashboard
- [ ] Machine learning recommendations
- [ ] Multi-currency support
- [ ] Advanced booking rules
- [ ] Integration with calendar systems
- [ ] Automated pricing optimization
- [ ] Guest communication system
- [ ] Property management tools
- [ ] Revenue management features

### Performance Improvements
- [ ] Database query optimization
- [ ] Caching strategy enhancement
- [ ] API response time optimization
- [ ] Image processing optimization
- [ ] Real-time notification system

---

**Built with ❤️ by the Enterprise Team**

