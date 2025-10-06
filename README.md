# SYSC 4806 – AddressBook (Labs 4–5)

Spring Boot web app with REST endpoints + Thymeleaf view, wired for CI (GitHub Actions) and ready for CD to Azure App Service.

## Run locally
```bash
mvn spring-boot:run
# then use src/test/http/lab5-requests.http or curl
```

## CI status
![Build](https://github.com/USER/REPO/actions/workflows/maven.yml/badge.svg?branch=main)

Replace `USER/REPO` with your GitHub handle and repo name.

## Azure
After linking this repo to Azure App Service (Windows, Java 17), GitHub Actions deploys on push to `main`.
Your app will be at:
```
https://<app-name>.azurewebsites.net
```
