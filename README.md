# Temporal Boot - Spring Boot Application with Temporal Workflows

A comprehensive Spring Boot application demonstrating Temporal workflow orchestration with multiple workers and business use cases.

## 🏗️ Architecture Overview

This application implements a microservices architecture using **Temporal** for workflow orchestration with the following components:

### Core Components

- **Spring Boot Application** (`TemporalBootApplication`) - Main application with REST APIs
- **Temporal Workers** - Separate worker processes for different business domains
- **Temporal Workflows** - Business logic orchestration
- **Temporal Activities** - Individual business operations
- **H2 Database** - In-memory database for transaction storage

### Business Use Cases

1. **Banner Message Publishing** - Asynchronous banner message creation and publishing
2. **Payment Scheduling** - Scheduled payment processing with retry mechanisms

## 🚀 Getting Started

### Prerequisites

- Java 21
- Gradle 8.x
- Docker & Docker Compose (for containerized deployment)
- Temporal Server (local or remote)

### Local Development Setup

#### 1. Start Temporal Server

```bash
# Using Docker (recommended)
docker run --rm -p 7233:7233 temporalio/auto-setup:1.20.1

# Or using Temporal CLI
temporal server start-dev
```

#### 2. Clone and Build the Application

```bash
git clone <repository-url>
cd temporal-boot
./gradlew build
```

#### 3. Run the Application Components

The application can be run in different modes using Spring profiles:

**Main Application (REST APIs):**
```bash
./gradlew bootRun --args='--spring.profiles.active=app'
```

**Banner Message Worker:**
```bash
./gradlew bootRun --args='--spring.profiles.active=publish-banner-worker'
```

**Payment Schedule Worker:**
```bash
./gradlew bootRun --args='--spring.profiles.active=payment-schedule-worker'
```

#### 4. Using Custom Gradle Tasks

The project includes custom Gradle tasks for running workers:

```bash
# Run the banner message worker
./gradlew runWorker

# Run specific workers using profiles
./gradlew bootRun --args='--spring.profiles.active=publish-banner-worker'
./gradlew bootRun --args='--spring.profiles.active=payment-schedule-worker'
```

### Docker Deployment

#### 1. Build Docker Images

```bash
# Build all images
docker-compose build

# Or build individual components
docker build -f Dockerfile.app -t temporal-boot-app .
docker build -f Dockerfile.PublishBannerMessageWorker -t temporal-boot-banner-worker .
docker build -f Dockerfile.SchedulePaymentWorker -t temporal-boot-payment-worker .
```

#### 2. Run with Docker Compose

```bash
docker-compose up
```

This will start:
- Main application on port 8083
- Banner message worker on port 8081
- Payment schedule worker on port 8082

## 📁 Project Structure

```
temporal-boot/
├── src/main/java/com/github/sardul3/temporal_boot/
│   ├── app/                           # Main application
│   │   ├── TemporalBootApplication.java
│   │   ├── services/                  # Business services
│   │   ├── repos/                     # Data repositories
│   │   └── exceptions/                # Custom exceptions
│   ├── api/                          # REST API layer
│   │   ├── controller/               # REST controllers
│   │   ├── dtos/                    # Data transfer objects
│   │   └── config/                  # API configuration
│   ├── workers/                      # Temporal workers
│   │   ├── PublishBannerMessageWorker.java
│   │   └── SchedulePaymentWorker.java
│   └── common/                       # Shared components
│       ├── workflows/               # Temporal workflow interfaces & implementations
│       ├── activities/              # Temporal activity interfaces & implementations
│       ├── models/                  # Domain models
│       ├── config/                  # Temporal configuration
│       └── utils/                   # Utility classes
├── src/main/resources/
│   ├── application.yml              # Main configuration
│   ├── application-app.yml          # App profile configuration
│   ├── application-publish-banner-worker.yml
│   └── application-payment-schedule-worker.yml
├── docker-compose.yml               # Docker orchestration
├── Dockerfile.app                   # Main app Dockerfile
├── Dockerfile.PublishBannerMessageWorker
├── Dockerfile.SchedulePaymentWorker
└── build.gradle                     # Gradle build configuration
```

## 🔄 Workflow Design

### 1. Banner Message Publishing Workflow

**Purpose:** Asynchronously create and publish banner messages

**Components:**
- **Workflow:** `PublishBannerMessageWorkflow`
- **Activities:** `PublishBannerMessageActivities`
- **Worker:** `PublishBannerMessageWorker`
- **Task Queue:** `BANNER_MESSAGE_QUEUE`

**API Endpoints:**
- `POST /api/banner/name` - Submit new banner message
- `GET /api/banner/name/{id}/status` - Check workflow status

### 2. Payment Scheduling Workflow

**Purpose:** Schedule and process payments with retry mechanisms

**Components:**
- **Workflow:** `SchedulePaymentWorkflow`
- **Activities:** `SchedulePaymentActivities`
- **Worker:** `SchedulePaymentWorker`
- **Task Queue:** `PAYMENT_SCHEDULE_QUEUE`

**API Endpoints:**
- `POST /api/transactions/schedule` - Schedule a payment
- `POST /api/transactions/schedule/{id}/fast-forward` - Fast-forward scheduled payment
- `POST /api/transactions/schedule/{id}/cancel` - Cancel scheduled payment

## ⚙️ Configuration

### Temporal Configuration

The application uses a comprehensive Temporal configuration system:

```yaml
temporal-config:
  server: localhost:7233
  namespace: learn-temp-boot
  workflows:
    paymentSchedulingWorkflow:
      prefix: "PSW"
      keyGenerationStrategy: "UUID"
      versions:
        v1:
          executionTimeout: 60s
          runTimeout: 7200s
          taskQueue: "PAYMENT_SCHEDULE_QUEUE"
    publishBannerMessageWorkflow:
      prefix: "PBW"
      keyGenerationStrategy: "UUID"
      versions:
        default:
          executionTimeout: 60s
          runTimeout: 7200s
          taskQueue: "BANNER_MESSAGE_QUEUE"
  activities:
    # Activity configurations with retry policies
  workers:
    # Worker configurations
  current-versions:
    # Version management for workflows and activities
```

### Database Configuration

- **H2 In-Memory Database** for development
- **JPA/Hibernate** for data persistence
- **H2 Console** enabled for database inspection

## 🧪 Testing

### Run Tests

```bash
./gradlew test
```

### Health Checks

The application exposes health endpoints:

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information

## 🔧 Development

### Key Features

1. **Multi-Profile Support** - Different Spring profiles for different components
2. **Temporal Integration** - Full Temporal SDK integration with Spring Boot
3. **Docker Support** - Containerized deployment with Docker Compose
4. **Configuration Management** - Centralized configuration with profiles
5. **Health Monitoring** - Spring Boot Actuator for monitoring

### Best Practices Implemented

- **Workflow Determinism** - All workflows are deterministic
- **Activity Retry Policies** - Configurable retry mechanisms
- **Version Management** - Workflow and activity versioning
- **Separation of Concerns** - Clear separation between workflows and activities
- **Spring Integration** - Full Spring Boot integration with Temporal

## 🚨 Troubleshooting

### Common Issues

1. **Temporal Server Connection**
   - Ensure Temporal server is running on `localhost:7233`
   - Check network connectivity if using remote Temporal

2. **Worker Not Starting**
   - Verify correct Spring profile is active
   - Check Temporal server connectivity
   - Review worker configuration in `application.yml`

3. **Workflow Execution Issues**
   - Check workflow determinism (no random operations)
   - Verify activity implementations are registered
   - Review task queue configurations

### Logs

Application logs provide detailed information about:
- Workflow execution status
- Activity execution and retries
- Worker startup and registration
- Temporal server connectivity

## 📚 Additional Resources

- [Temporal Documentation](https://docs.temporal.io/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Temporal Java SDK](https://github.com/temporalio/sdk-java)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.