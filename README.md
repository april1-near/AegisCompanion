# AegisCompanion - 睿伴社区服务平台

![License](https://img.shields.io/badge/license-MIT-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-green)
![Vue3](https://img.shields.io/badge/Vue-3.x-brightgreen)

AegisCompanion 是一款面向现代智慧社区的综合性服务平台，整合了资源预约、工单管理、医疗服务和AI助手等核心功能，助力社区数字化转型。

## 🌟 功能特性

### 居民服务
- 🅿️ 智能停车位预约与管理
- 🏟️ 社区活动室在线预约（会议室/运动场馆）
- 🏥 社区医生在线预约与排班查询
- 🔧 智能工单系统（自动分配+状态跟踪）
- 💬 AI智能助手（基于Spring AI的聊天机器人）

### 管理功能
- 📝 预约审批与~~黑名单管理~~
- 👨‍🔧 维修工单调度与追踪
- 🔐 细粒度权限控制系统

## 🛠️ 技术栈

### 后端
- **核心框架**: Spring Boot 3.4.2
- **AI集成**: Spring AI + 智谱大模型
- **数据库**: MySQL 8.0 + MyBatis Plus
- **消息队列**: RabbitMQ
- **缓存**: Redis 6
- **安全认证**: JWT + Spring Security
- **API文档**: SpringDoc OpenAPI 2.3.0

### 前端
- Vue3 + Vite + Element Plus
- WebSocket实时通信
- 响应式布局

### 基础设施
- Docker + Docker Compose
- Nginx反向代理
- 阿里云ECS部署

## 🚀 快速启动

### 环境要求
- Docker 20.10+
- JDK 17
- Node.js 16+

### 使用Docker Compose部署
```bash
# 1. 克隆仓库
git clone https://github.com/aprilLlie/AegisCompanion.git
cd AegisCompanion
# 2. 初始化环境配置
cp .env.example .env
# 编辑.env文件设置CHAT_API_KEY（需申请智谱API密钥）
nano .env  # 或使用其他编辑器
# 3. 构建并启动容器
docker-compose up -d --build
# 4. 查看构建日志（可选）
docker-compose logs -f app
# 5. 验证服务状态
docker-compose ps
# 6. 停止服务
docker-compose down

```

服务启动后访问：
- 后端API文档：http://localhost:8090/swagger-ui.html
- 前端页面：http://localhost:80
- RabbitMQ控制台：http://localhost:15672 (guest/guest)
- Redis客户端：redis-cli -h localhost -p 6379

### 手动构建
```bash
# 后端
cd AegisCompanion
mvn clean package
java -jar target/ac-smart-community-0.0.1-SNAPSHOT.jar

# 前端
cd frontend
pnpm install
pnpm run build
nginx -c nginx.conf
```

## 📂 目录结构
```
.
├── AegisCompanion          # Spring Boot后端项目
│   ├── src                 # 源代码
│   └── pom.xml             # Maven配置
├── frontend                # Vue前端项目
│   ├── src                 # 前端源码
│   └── vite.config.ts      # Vite配置
├── mysql                   # 数据库脚本
│   └── init-scripts        # 初始化SQL
├── docker-compose.yml      # 容器编排配置
├── Dockerfile              # 构建脚本Dockerfile
└── start.sh                # 快速启动脚本
```

## 📚 相关文档
- [数据库设计文档](./AegisCompanion.sql)
- [系统UML设计](./系统uml.md)
- [架构设计文档](./智慧社区平台完整架构设计💛.md)

## 💡 使用说明
1. **默认账户**：
   - 管理员：system_user/admin@1234
   - 居民用户：TestUser/Test1234!
   - 维修工：pipe_wang/rI87B2FuPm$

2. **AI助手使用**：
   - 通过WebSocket连接 ws://localhost:8090/chat
   - 支持自然语言指令：
     - "预约明天下午3点的羽毛球场"
     - "查看我的停车预约"
     - "提交水管维修工单"

3. **注意事项**：
   - 首次启动需等待MySQL初始化完成（约1-2分钟）
   - 生产环境请修改默认密码和JWT密钥
   - 聊天功能需配置有效的智谱API密钥

---

**License**: MIT © 2025 aprilLie Team