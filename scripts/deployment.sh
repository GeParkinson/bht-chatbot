#!/usr/bin/env bash
./gradlew war
cd src/main/docker
docker-compose down && docker-cmpose up --build