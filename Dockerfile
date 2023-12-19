FROM maven:3.9.4-eclipse-temurin-21-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /sticky-note

COPY --from=build target/*.jar sticky-note.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "sticky-note.jar"]