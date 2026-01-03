# ===== Build Stage =====
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build

# Copy entire repo to include parent POM
COPY . .

# Build only the child module and also build parent if needed
RUN mvn -q -pl shopping-cart-service -am clean package -DskipTests

# ===== Run Stage =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy final JAR
COPY --from=build /build/shopping-cart-service/target/*.jar shopping-cart-service.jar
#COPY --from=build /build/shopping-cart-service/src/main/resources/application.yaml /app/application.yaml

EXPOSE 8083

ENTRYPOINT ["java","-jar","/app/shopping-cart-service.jar"]