# Currency Exchange Service

This project is a Spring Boot application that provides a REST API for managing currencies and retrieving their exchange rates. It integrates with an external exchange rate provider API and uses a PostgreSQL database for persistent storage. Docker Compose is used to set up the database.

---

## **Features**

- **Get a list of tracked currencies**: Retrieve all currencies currently being tracked by the application.
- **Add a new currency**: Add a currency to the system for tracking exchange rates.
- **Get exchange rates**: Retrieve exchange rates for a specific currency.
- **Convert amount**: Convert amount regarding exchange rates
- **Scheduled updates**: Automatically fetch and update exchange rates for all tracked currencies every hour.
- **Caching**: Uses Spring Cache to reduce redundant API calls.
- **Rate limiting**: Limits the frequency of requests to the external API using Resilience4j.

---

## **Technologies**

- **Java 17**
- **Spring Boot 3.3.6**
- **Spring Cache**
- **Spring Data JPA**
- **PostgreSQL**
- **H2 database**
- **Liquibase**
- **Open API Documentation**
- **Actuator**
- **Resilience4j**
- **Lombok**
- **Maven**
- **Junit5**
- **Spring Test**
- **Docker**

---

## **API Documentation**:
   - Open [Swagger UI](http://localhost:8080/swagger-ui.html) in your browser for API  if you run without a docker.
   - Open [Swagger UI](http://localhost:8090/swagger-ui.html) in your browser if with

---

## Endpoints

### 1. **Get All Currencies**
- **URL**: `/api/v1/currencies`
- **Method**: GET
- **Response**:
  ```json
  [
    "USD",
    "EUR",
    "UAH"
  ]
  ```

### 2. **Add New Currency**
- **URL**: `/api/v1/currencies`
- **Method**: POST
- **Request Body**:
  ```json
  {
    "currencies": ["USD", "EUR"]
  }
  ```

- **Response**: `200 OK`

### 3. **Get Exchange Rates**
- **URL**: `/api/v1/exchange-rates/{baseCurrency}`
- **Method**: GET
- **Response**:
  ```json
  {
    "baseCurrency": "USD",
    "rates": {
      "EUR": 0.85,
      "UAH": 36.5
    }
  }
  ```

---
