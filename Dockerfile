# Etapa de compilación
FROM gradle:jdk21-alpine AS build
WORKDIR /app
COPY build.gradle .
COPY gradlew .
COPY gradle gradle
COPY src src
RUN ./gradlew build -x test

# Etapa de ejecución
FROM eclipse-temurin:21-jre-alpine AS run
WORKDIR /app
COPY --from=build /app/build/libs/*SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]