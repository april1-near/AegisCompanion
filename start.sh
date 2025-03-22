#!/bin/sh
# 启动Spring Boot应用
java -Dspring.profiles.active=prod -jar /app/app.jar 

# 启动Nginx
nginx -g "daemon off;" &
