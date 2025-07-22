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

**Product Data Improvement Worker:**
```bash
./gradlew bootRun --args='--spring.profiles.active=product-data-improvement-worker'
```

#### 4. Using Custom Gradle Tasks

The project includes custom Gradle tasks for running workers:

```bash
# Run the banner message worker
./gradlew runWorker

# Run specific workers using profiles
./gradlew bootRun --args='--spring.profiles.active=publish-banner-worker'
./gradlew bootRun --args='--spring.profiles.active=payment-schedule-worker'
./gradlew bootRun --args='--spring.profiles.active=product-data-improvement-worker'
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

**Example Usage:**
```bash
# Submit new banner message
curl -X POST http://localhost:8080/api/banner/name \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: banner-request-123" \
  -d '{
    "message": "Welcome to our new website!"
  }'

# Check banner message status
curl -X GET http://localhost:8080/api/banner/name/PBW-uuid-generated-id/status \
  -H "X-Correlation-ID: status-check-456"
```

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

**Example Usage:**
```bash
# Schedule a payment
curl -X POST http://localhost:8080/api/transactions/schedule \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: payment-request-123" \
  -d '{
    "from": "account-123",
    "to": "account-456", 
    "amount": 150.50,
    "when": "2024-01-15T10:30:00"
  }'

# Fast-forward scheduled payment
curl -X POST http://localhost:8080/api/transactions/schedule/PSW-uuid-generated-id/fast-forward \
  -H "X-Correlation-ID: fast-forward-456"

# Cancel scheduled payment
curl -X POST http://localhost:8080/api/transactions/schedule/PSW-uuid-generated-id/cancel \
  -H "X-Correlation-ID: cancel-789"
```

### 3. Product Data Improvement Workflow

**Purpose:** Process CSV files containing GTINs and improve product data using AI

**Components:**
- **Workflow:** `ProductDataImprovementWorkflow`
- **Activities:** `ProductDataImprovementActivities`
- **Worker:** `ProductDataImprovementWorker`
- **Task Queue:** `PRODUCT_IMPROVEMENT_QUEUE`

**API Endpoints:**
- `POST /api/product/improve` - Start product data improvement workflow
- `GET /api/product/improve/{id}/status` - Check workflow status

**Sample CSV File Setup:**

Create a file named `sample_products.csv` in your project root:

```csv
gtin
1234567890123
1234567890124
1234567890125
1234567890126
1234567890127
1234567890128
1234567890129
1234567890130
1234567890131
1234567890132
```

**Example Usage:**
```bash
# Start product data improvement workflow
curl -X POST http://localhost:8080/api/product/improve \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: product-improvement-123" \
  -d '{
    "fileReference": {
      "path": "sample_products.csv",
      "mimeType": "text/csv",
      "fileSizeBytes": 512
    },
    "useCase": {
      "name": "ProductDataImprovementWorkflow"
    }
  }'

# Check workflow status
curl -X GET http://localhost:8080/api/product/improve/PDI-uuid-generated-id/status \
  -H "X-Correlation-ID: status-check"
```

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

### API Testing

#### Banner Message Publishing

**Submit Different Types of Banner Messages:**

```bash
# Simple banner message
curl -X POST http://localhost:8080/api/banner/name \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: simple-banner" \
  -d '{
    "message": "Welcome to our new website!"
  }'

# Promotional banner message
curl -X POST http://localhost:8080/api/banner/name \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: promo-banner" \
  -d '{
    "message": "Special promotion: 50% off all items!"
  }'

# Banner message with special characters
curl -X POST http://localhost:8080/api/banner/name \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: special-banner" \
  -d '{
    "message": " Happy New Year! 🎊 Special offers available now!"
  }'
```

**Check Workflow Status:**

```bash
# Replace PBW-uuid-generated-id with the actual ID from the response
curl -X GET http://localhost:8080/api/banner/name/PBW-uuid-generated-id/status \
  -H "X-Correlation-ID: status-check"
```

#### Payment Scheduling

**Schedule Different Types of Payments:**

```bash
# Small payment
curl -X POST http://localhost:8080/api/transactions/schedule \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: small-payment" \
  -d '{
    "from": "checking-account",
    "to": "savings-account",
    "amount": 25.00,
    "when": "2024-01-20 09:00"
  }'

# Large payment
curl -X POST http://localhost:8080/api/transactions/schedule \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: large-payment" \
  -d '{
    "from": "business-account",
    "to": "vendor-account",
    "amount": 5000.00,
    "when": "2024-01-25 14:30"
  }'

# Immediate payment (past date)
curl -X POST http://localhost:8080/api/transactions/schedule \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: immediate-payment" \
  -d '{
    "from": "user-account-001",
    "to": "merchant-account-002",
    "amount": 99.99,
    "when": "2024-01-10 12:00"
  }'
```

**Manage Scheduled Payments:**

```bash
# Fast-forward a scheduled payment (execute immediately)
curl -X POST http://localhost:8080/api/transactions/schedule/PSW-uuid-generated-id/fast-forward \
  -H "X-Correlation-ID: fast-forward"

# Cancel a scheduled payment
curl -X POST http://localhost:8080/api/transactions/schedule/PSW-uuid-generated-id/cancel \
  -H "X-Correlation-ID: cancel"
```

#### Product Data Improvement

**Start Product Data Improvement Workflow:**

```bash
# Basic product data improvement
curl -X POST http://localhost:8080/api/product/improve \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: product-improvement" \
  -d '{
    "fileReference": {
      "path": "sample_products.csv",
      "mimeType": "text/csv",
      "fileSizeBytes": 512
    },
    "useCase": {
      "name": "ProductDataImprovementWorkflow"
    }
  }'

# Product data improvement with different file
curl -X POST http://localhost:8080/api/product/improve \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: product-improvement-large" \
  -d '{
    "fileReference": {
      "path": "large_product_catalog.csv",
      "mimeType": "text/csv",
      "fileSizeBytes": 2048
    },
    "useCase": {
      "name": "ProductDataImprovementWorkflow"
    }
  }'
```

**Check Workflow Status:**

```bash
# Replace PDI-uuid-generated-id with the actual ID from the response
curl -X GET http://localhost:8080/api/product/improve/PDI-uuid-generated-id/status \
  -H "X-Correlation-ID: status-check"
```

#### Complete Testing Workflow

**Test Banner Publishing Workflow:**

```bash
# 1. Submit banner message
RESPONSE=$(curl -s -X POST http://localhost:8080/api/banner/name \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: test-banner" \
  -d '{
    "message": "Test banner message"
  }')

# 2. Extract tracking ID
TRACKING_ID=$(echo $RESPONSE | grep -o '"requestTrackingId":"[^"]*"' | cut -d'"' -f4)

echo "Banner tracking ID: $TRACKING_ID"

# 3. Check status
curl -X GET http://localhost:8080/api/banner/name/$TRACKING_ID/status \
  -H "X-Correlation-ID: test-status"
```

**Test Payment Scheduling Workflow:**

```bash
# 1. Schedule a payment
RESPONSE=$(curl -s -X POST http://localhost:8080/api/transactions/schedule \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: test-payment" \
  -d '{
    "from": "test-account-1",
    "to": "test-account-2",
    "amount": 100.00,
    "when": "2024-01-15T10:30:00"
  }')

# 2. Extract payment schedule ID
PAYMENT_ID=$(echo $RESPONSE | grep -o '"paymentScheduleId":"[^"]*"' | cut -d'"' -f4)

echo "Payment schedule ID: $PAYMENT_ID"

# 3. Fast-forward the payment
curl -X POST http://localhost:8080/api/transactions/schedule/$PAYMENT_ID/fast-forward \
  -H "X-Correlation-ID: test-fast-forward"
```

**Test Product Data Improvement Workflow:**

```bash
# 1. Start product data improvement workflow
RESPONSE=$(curl -s -X POST http://localhost:8080/api/product/improve \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: test-improvement" \
  -d '{
    "fileReference": {
      "path": "sample_products.csv",
      "mimeType": "text/csv",
      "fileSizeBytes": 512
    },
    "useCase": {
      "name": "ProductDataImprovementWorkflow"
    }
  }')

# 2. Extract workflow ID
WORKFLOW_ID=$(echo $RESPONSE | grep -o '"workflowId":"[^"]*"' | cut -d'"' -f4)

echo "Product improvement workflow ID: $WORKFLOW_ID"

# 3. Check status
curl -X GET http://localhost:8080/api/product/improve/$WORKFLOW_ID/status \
  -H "X-Correlation-ID: test-status"
```

### Testing Notes

- **Port**: Application runs on port `8080`
- **Correlation ID**: Optional but recommended for request tracking
- **Date Format**: Use ISO 8601 format for payment scheduling (`YYYY-MM-DDTHH:mm:ss`)
- **Workers**: Ensure all workers are running for complete workflow testing
- **CSV Files**: Make sure sample CSV files are accessible to the application
- **File Paths**: Use relative paths for CSV files in the project root directory

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