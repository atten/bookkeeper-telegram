#!/usr/bin/env bash
export $(grep -v '^#' .env | xargs)
docker-compose pull
docker-compose up -d