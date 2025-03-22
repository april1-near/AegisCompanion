# 使用多阶段构建来优化镜像大小
# 第一阶段：构建后端
FROM eclipse-temurin:17-jdk-jammy AS backend-builder

# 替换为阿里云镜像源
RUN sed -i "s@http://.*archive.ubuntu.com@http://mirrors.aliyun.com@g" /etc/apt/sources.list && \
    sed -i "s@http://.*security.ubuntu.com@http://mirrors.aliyun.com@g" /etc/apt/sources.list

# 安装 Maven
RUN apt-get update && \
    apt-get install -y --no-install-recommends maven && \
    rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制后端代码
COPY ./AegisCompanion /app

# 构建后端应用
RUN mvn clean package -DskipTests

# 第二阶段：构建前端
FROM node:20 AS frontend-builder

# 设置工作目录
WORKDIR /app

# 复制前端代码
COPY ./frontend /app

# 安装依赖并构建前端
RUN npm install -g pnpm && \
    pnpm install && \
    pnpm run build

# 第三阶段：最终镜像
FROM eclipse-temurin:17-jre-jammy

# 替换为阿里云镜像源
RUN sed -i "s@http://.*archive.ubuntu.com@http://mirrors.aliyun.com@g" /etc/apt/sources.list && \
    sed -i "s@http://.*security.ubuntu.com@http://mirrors.aliyun.com@g" /etc/apt/sources.list

# 安装依赖
RUN apt-get update && \
    apt-get install -y --no-install-recommends nginx dos2unix && \
    rm -rf /var/lib/apt/lists/*

# 设置时区
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo Asia/Shanghai > /etc/timezone

# 复制启动脚本并设置权限
COPY start.sh /start.sh
RUN dos2unix /start.sh && \
    chmod 755 /start.sh

# 设置工作目录
WORKDIR /app

# 从第一阶段复制构建好的后端应用
COPY --from=backend-builder /app/target/*.jar /app/app.jar

# 从第二阶段复制构建好的前端应用
COPY --from=frontend-builder /app/dist /usr/share/nginx/html
COPY ./frontend/nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80 8090

# 使用 root 用户运行
ENTRYPOINT ["/start.sh"]

