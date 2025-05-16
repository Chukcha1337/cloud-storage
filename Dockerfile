# Используем минимальный образ с JDK 21
FROM eclipse-temurin:21-jdk

# Устанавливаем рабочую директорию
WORKDIR /app

RUN mkdir -p /app/logs

# Копируем jar-файл (название может отличаться)
COPY build/libs/cloud-filestorage-0.0.1-SNAPSHOT.jar app.jar

# Указываем точку входа
ENTRYPOINT ["java", "-jar", "app.jar"]