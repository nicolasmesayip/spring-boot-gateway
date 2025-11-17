# ===== Build Stage =====
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build

# Copy entire repo to allow parent pom + modules resolution
COPY . .

# Build only this module
RUN mvn -q -pl api-gateway -am clean package -DskipTests

# ===== Run Stage =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the final JAR
COPY --from=build /build/api-gateway/target/*.jar api-gateway.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/api-gateway.jar"]