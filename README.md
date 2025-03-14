### Crypto Analyzer Application - README

#### Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation and Setup](#installation-and-setup)
- [Running the Application](#running-the-application)
- [Endpoints](#endpoints)
- [Rate Limiting](#rate-limiting)
- [Testing](#testing)
- [Kubernetes Deployment](#kubernetes-deployment)

---

### Overview

The **Crypto Analyzer** is a Spring Boot-based application that provides analytics on cryptocurrency data. It processes cryptocurrency prices from various coins (such as BTC, DOGE, ETH, LTC, and XRP) and exposes REST APIs for users to query data such as the highest normalized price range or other statistics.

The app reads cryptocurrency data from CSV files and stores it into a PostgreSQL database. It then allows users to query for normalized price ranges, historical prices, and other related information via HTTP endpoints.

### Features
- Read and process cryptocurrency data from CSV files
- Store and query cryptocurrency data using a PostgreSQL database
- Expose RESTful APIs to access cryptocurrency statistics
- Calculate the highest normalized price range for a specific day
- Rate limiting to prevent abuse by users (based on IP address)

### Technologies Used

- **Java 17**
- **Spring Boot 3.3.4** (Data JPA, Web)
- **PostgreSQL** for database
- **OpenCSV** for CSV file parsing
- **Bucket4j** for rate limiting
- **JaCoCo** for code coverage
- **LomBok**
- **Docker & Kubernetes** for containerization and deployment

### Installation and Setup

#### Prerequisites
- **Java 17** or higher
- **Maven**
- **PostgreSQL** installed locally or running in Docker
- **Docker & Kubernetes** (for deployment)

#### Clone the repository:
```bash
git clone https://github.com/personamoris/crypto-analyzer.git
cd crypto-analyzer
```

#### Setup PostgreSQL
1. Install PostgreSQL and create a new database:
    ```sql
    CREATE DATABASE crypto-analyzer;
    ```
2. Update the `application.properties` file in `src/main/resources/` with your PostgreSQL credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/crypto-analyzer
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```

#### Build and Run the Application:
1. Build the application using Maven:
    ```bash
    mvn clean install
    ```
2. Run the application:
    ```bash
    mvn spring-boot:run
    ```

### Running the Application

Once the application is running, it will:
1. Load and process CSV files containing cryptocurrency data (from `src/main/resources/prices`).
2. Expose REST endpoints for retrieving various cryptocurrency statistics.

The default port for the application is **8080**. You can access the endpoints at `http://localhost:8080/`.

### Endpoints

#### 1. Retrieves statistics for a specific cryptocurrency based on its symbol.
- **URL**: `/api/crypto/{symbol}/stats-string`
- **Method**: `GET`
- **Query Params**:
  - `symbol` (String, symbol)
  
Example:
```bash
curl -X GET "http://localhost:8080/api/crypto/BTC/stats-string"
```

#### 2. Get a sorted list of cryptos based on normalized range
- **URL**: `/api/crypto/highest-range-string`
- **Method**: `GET`

Example:
```bash
curl "http://localhost:8080/api/crypto/highest-range-string"
```

#### 3. Get the cryptocurrency with the highest normalized range for a specific day
- **URL**: `/api/crypto/{date}/highest-normalized-range-string`
- **Method**: `GET`
- **Query Params**:
  - `date` (String, required) - Date in format `yyyy-dd-MM`
  
Example:
```bash
curl -X GET "http://localhost:8080/api/crypto/2022-01-01/highest-normalized-range-string"
```

### Rate Limiting
The application uses **Bucket4j** to limit requests based on the client’s IP address. The rate limit is set to allow a maximum of 10 requests per minute for each IP address. If a user exceeds this limit, they will receive a `429 Too Many Requests` error.

### Testing

The project includes unit tests to verify the functionality of key services and methods, such as calculating normalized ranges for cryptos. To run the tests, use:

```bash
mvn test
```

The project also includes **JaCoCo** for code coverage. After running the tests, a coverage report is generated in the `target/site/jacoco/index.html` file.

### Kubernetes Deployment

To containerize and deploy the application in Kubernetes, follow these steps:

#### 1. Build Docker Image
Ensure you have Docker installed, then run the following command to build the Docker image:
```bash
docker build -t crypto-analyzer .
```

#### 2. Kubernetes Configuration
Ensure you have a Kubernetes cluster running (Minikube, Docker Desktop, etc.). 

The Kubernetes deployment YAML files are stored in the `k8s/` folder.

#### 3. Deploy Application
Apply the Kubernetes deployment and service configuration:
```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

#### 4. Access the Application
Once deployed, access the application via the service URL provided by Kubernetes.


---
