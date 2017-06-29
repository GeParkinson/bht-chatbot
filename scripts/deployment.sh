#!/usr/bin/env bash
./gradlew war
cd src/main/docker
# FIX: https://stackoverflow.com/questions/35760760/writable-folder-permissions-in-docker
chown -R 1000:beuthbot  wildfly/volumes/deployments
docker-compose down && docker-compose up --build -d