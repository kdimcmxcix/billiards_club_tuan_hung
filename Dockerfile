# Sử dụng base image Java 17
FROM eclipse-temurin:17

# Copy file .jar đã build sẵn từ thư mục target/
COPY target/billiards_club_tuan_hung-0.0.1-SNAPSHOT.jar app.jar

# Câu lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app.jar"]
