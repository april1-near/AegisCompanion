#!/bin/sh

nginx -g "daemon off;" &
exec java -Dspring.profiles.active=prod -jar /app/app.jar
