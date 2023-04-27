
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



# # Imagen base
# FROM openjdk:17-jdk-slim

# # Directorio de trabajo
# WORKDIR /app

# # Copia del código fuente
# COPY . .

# # Instalación de maven
# RUN apt-get update && \
#     apt-get install -y maven && \
#     mvn package && \
#     apt-get remove -y maven && \
#     apt-get autoremove -y && \
#     rm -rf /var/lib/apt/lists/*

# # Eliminación de dependencias no necesarias
# RUN rm -rf /app/target/dependency-jars

# # Eliminación de código innecesario
# RUN rm -rf /app/src /app/*.java

# # Puerto expuesto
# EXPOSE 8080

# # Comando de inicio
# CMD ["java", "-jar", "/app/target/my-application.jar"]





# # Usamos una imagen base con OpenJDK 17
# FROM openjdk:17-jdk-alpine AS build

# # Establecemos el directorio de trabajo
# WORKDIR /app

# # Copiamos los archivos pom.xml y src al contenedor
# COPY pom.xml src/ /app/

# # Descargamos y cacheamos las dependencias de Maven
# RUN apk --no-cache add maven && mvn -B dependency:go-offline

# # Compilamos la aplicación
# RUN mvn -B package --no-transfer-progress

# # Creamos una imagen nueva con JRE 17 para ejecutar la aplicación
# FROM openjdk:17-jre-alpine

# # Establecemos el directorio de trabajo
# WORKDIR /app

# # Copiamos el archivo .jar de la compilación anterior al contenedor
# COPY --from=build /app/target/*.jar /app/app.jar

# # Eliminamos Maven y todas las dependencias
# RUN rm -rf /root/.m2 && apk del maven

# # Ejecutamos la aplicación
# CMD ["java", "-jar", "app.jar"]

