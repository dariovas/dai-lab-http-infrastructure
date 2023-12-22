# Utilisation de l'image de base avec Maven
FROM maven AS build

# Définition du répertoire de travail dans le conteneur
WORKDIR /app

# Copie du fichier pom.xml et des sources dans le conteneur
COPY pom.xml /app
COPY src /app/src

# Compilation de l'application avec Maven
RUN mvn -B -DskipTests clean package

# Utilisation d'une image plus légère pour exécuter l'application
FROM alpine:latest
RUN apk add --no-cache openjdk20

# Définition du répertoire de travail dans le conteneur final
WORKDIR /app

# Copie du fichier JAR généré depuis l'étape précédente
COPY --from=build /app/target/*.jar /app/app.jar

# Commande pour exécuter l'application
CMD ["java", "-jar", "app.jar"]