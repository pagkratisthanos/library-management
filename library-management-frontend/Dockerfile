# Stage 1 — build the static bundle
FROM node:22-alpine AS build
WORKDIR /app

# copy the manifests first so this layer is cached until a dependency changes
COPY package.json package-lock.json ./
RUN npm ci

COPY . .

# Vite inlines environment variables at build time, so the API address has to be
# known here rather than when the container starts. That is why it is an ARG.
ARG VITE_API_URL=http://localhost:8080/api/v1
ENV VITE_API_URL=$VITE_API_URL
RUN npm run build

# Stage 2 — serve it
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
