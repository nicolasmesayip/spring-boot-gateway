# ===== Build Stage =====
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build

# Copy entire repo to allow parent pom + modules resolution
COPY . .

# Build only this module
RUN mvn -q -pl authentication -am clean package -DskipTests

# ===== Run Stage =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the final JAR
COPY --from=build /build/authentication/target/*.jar authentication.jar

EXPOSE 8090

ENTRYPOINT ["java","-jar","/app/authentication.jar"]