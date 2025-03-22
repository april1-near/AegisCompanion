# 后端构建阶段
FROM eclipse-temurin:17-jdk as backend
WORKDIR /app
COPY ./AegisCompanion/target/*.jar app.jar

# 前端构建阶段
FROM node:20-alpine as frontend
WORKDIR /app
COPY ./frontend/dist /app/dist

# 最终镜像
FROM eclipse-temurin:17-jdk
RUN apt-get update && \
    apt-get install -y nginx && \
    rm -rf /var/lib/apt/lists/*

# 复制后端应用
COPY --from=backend /app/app.jar /app/app.jar

# 复制前端资源
COPY --from=frontend /app/dist /usr/share/nginx/html

# 复制Nginx配置
COPY ./frontend/nginx.conf /etc/nginx/conf.d/default.conf

# 启动脚本
COPY start.sh /start.sh
RUN chmod +x /start.sh

EXPOSE 80 8090

ENTRYPOINT ["/start.sh"]
