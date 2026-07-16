# Etapa 1: compila el proyecto y genera el jar ejecutable con Gradle.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copiamos primero el wrapper y los archivos de build (sin el código fuente)
# para que Docker cachee la descarga de dependencias mientras no cambien.
COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Etapa 2: imagen final, liviana, solo con el jar y un JRE.
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Usuario sin privilegios: si alguien logra ejecutar código dentro del
# contenedor, no queda corriendo como root.
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/build/libs/*.jar app.jar
RUN chown spring:spring app.jar
USER spring:spring

EXPOSE 8080

# Docker (y cualquier orquestador que lea este healthcheck) usa el mismo
# endpoint público que dejamos abierto en SecurityConfig.
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# Producción por defecto; docker-compose puede sobreescribirlo si hace falta.
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Xms256m", \
    "-Xmx512m", \
    "-jar", \
    "app.jar"]
