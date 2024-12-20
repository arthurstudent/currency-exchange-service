# stage 1 - dependency download layer
FROM maven:3.8.3-openjdk-17 AS dependencies
WORKDIR /build
COPY pom.xml ./
RUN mvn dependency:go-offline

# stage 2 - application build layer
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /build
COPY src ./src
COPY --from=dependencies /root/.m2 /root/.m2
COPY pom.xml ./
RUN mvn clean package

# stage 3 - application run layer
FROM openjdk:17
WORKDIR /app
COPY --from=build /build/target/currency-exchange-service*jar currency-exchange-service.jar
EXPOSE 8080
CMD ["java", "-jar", "currency-exchange-service.jar"]