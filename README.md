# Recipes API

Convenience wrapper for GitHub API to access recipes from the [recipes-repository](https://github.com/vilikin/recipes).

## Run locally

```
./gradlew run
```

## Run with Docker

```
docker-compose up -d app
```

## Deploying to Digital Ocean

Digital Ocean runs into errors with `docker-compose up -d app`.

For the droplet deployment, a separate Dockerfile-jar is provided.

1. Build jar file locally by running: `./gradlew shadowJar`
2. In the droplet, run `docker-compose up -d jar`
