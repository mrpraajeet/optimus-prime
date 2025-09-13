FROM openjdk:21-jdk-slim AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy

RUN apt-get update && \
    apt-get install -y primecount && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "app.jar"]