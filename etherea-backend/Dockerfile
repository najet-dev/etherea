# Utilise une image Java légère
FROM openjdk:17-jdk-slim

# Répertoire de travail dans le conteneur
WORKDIR /app

# Copier le JAR généré
COPY target/etherea-0.0.1-SNAPSHOT.jar etherea-0.0.1-SNAPSHOT.jar

# Exposer le port utilisé par l’application
EXPOSE 9090

# Lancer l’application
ENTRYPOINT ["java", "-jar", "etherea-0.0.1-SNAPSHOT.jar"]
