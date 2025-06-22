# Giai đoạn 1: Build project bằng Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy file .jar
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
