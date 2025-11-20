# ===== Build Stage =====
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build

# Copy entire repo to include parent POM
COPY . .

# Build only the child module and also build parent if needed
RUN mvn -q -pl products-service -am clean package -DskipTests

# ===== Run Stage =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy final JAR
COPY --from=build /build/products-service/target/*.jar products-service.jar
#COPY --from=build /build/products-service/src/main/resources/application.yaml /app/application.yaml

EXPOSE 8082

ENTRYPOINT ["java","-jar","/app/products-service.jar"]