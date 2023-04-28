
FROM maven:3.8.3-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests && \
    rm -rf /root/.m2/repository/* && \
    mvn dependency:purge-local-repository -DreResolve=false

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/backend_flight_booking-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
