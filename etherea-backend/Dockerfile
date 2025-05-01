# build avec Maven
FROM maven:3.8.5-openjdk-17 AS builder

# Définir le répertoire de travail
WORKDIR /app

# Copier tout le code source dans l'image
COPY . .

# Construire l'application (le .jar sera généré dans /app/target)
RUN mvn clean package -DskipTests

# image légère pour exécuter le .jar
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copier uniquement le JAR compilé depuis l'image builder
COPY --from=builder /app/target/etherea-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port
EXPOSE 9090

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
