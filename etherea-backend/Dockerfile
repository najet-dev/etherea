# compilation + exécution dans Java 21
FROM openjdk:21-jdk-slim

RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean

WORKDIR /app

# Copier tous les fichiers nécessaires au build
COPY . .

# Construire l'application (ignorer les tests)
RUN mvn clean package -DskipTests

# Exposer le port utilisé par l'application
EXPOSE 9090

# Lancer le .jar généré
ENTRYPOINT ["java", "-jar", "target/etherea-0.0.1-SNAPSHOT.jar"]
