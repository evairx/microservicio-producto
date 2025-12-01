# ───────────────────────────────
# STAGE 1: Build con Gradle
# ───────────────────────────────
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies || true

COPY src src

RUN ./gradlew --no-daemon clean build

# ───────────────────────────────
# STAGE 2: Imagen final
# ───────────────────────────────
FROM eclipse-temurin:17-jre

WORKDIR /app

EXPOSE 8080

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
