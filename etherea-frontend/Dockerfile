# Étape 1: Build de l'application Angular
FROM node:16 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier le package.json et installer les dépendances
COPY package*.json ./
RUN npm install

# Copier le code source Angular dans le conteneur
COPY . .

# Construire l'application Angular pour la production
RUN npm run build --prod

# Étape 2: Configurer NGINX pour servir l'application Angular
FROM nginx:alpine

# Copier le fichier nginx.conf dans le conteneur NGINX
COPY nginx/nginx.conf /etc/nginx/nginx.conf

# Copier les fichiers générés de la build Angular dans le répertoire NGINX
COPY --from=build /app/dist/ /usr/share/nginx/html

# Exposer le port sur lequel NGINX va écouter
EXPOSE 80

# Démarrer NGINX
CMD ["nginx", "-g", "daemon off;"]
