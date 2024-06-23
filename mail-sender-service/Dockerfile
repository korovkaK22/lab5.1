# Build stage
FROM maven:3.9.7 AS build
WORKDIR /app
COPY pom.xml /app
COPY .env /app/.env
RUN mvn dependency:resolve
COPY . /app
RUN mvn clean
RUN mvn package -DskipTests

# Run stage
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/.env .env
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]
