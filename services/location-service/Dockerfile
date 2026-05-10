# syntax=docker/dockerfile:1.7
FROM maven:3.9.9-eclipse-temurin-21 AS build

ARG SERVICE_PATH=services/location-service
WORKDIR /workspace

COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -pl ${SERVICE_PATH} -am package -DskipTests

FROM eclipse-temurin:21-jre

ARG SERVICE_PATH=services/location-service
WORKDIR /app

COPY --from=build /workspace/${SERVICE_PATH}/target/*.jar /app/app.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
