# Utilise une image Java officielle (runtime Java 17 ici)
FROM openjdk:21-jdk-slim

# Crée un dossier pour l'application
WORKDIR /app

# Copie le fichier JAR généré dans l'image
COPY target/etherea-0.0.1-SNAPSHOT.jar app.jar

# Expose le port sur lequel ton app tourne
EXPOSE 9090

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
