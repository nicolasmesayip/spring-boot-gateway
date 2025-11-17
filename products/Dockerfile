# ===== Build Stage =====
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build

# Copy entire repo to allow parent pom + modules resolution
COPY . .

# Build only this module
RUN mvn -q -pl products -am clean package -DskipTests

# ===== Run Stage =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the final JAR
COPY --from=build /build/products/target/*.jar products.jar
COPY --from=build /build/products/src/main/resources/application.yaml /app/application.yaml

EXPOSE 8082

ENTRYPOINT ["java","-jar","/app/products.jar"]