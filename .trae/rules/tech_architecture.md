# 技术架构与选型

本项目采用 Monorepo 架构，目录结构必须遵循以下逻辑：
- backend/
  - Java 后端（Spring Boot）
- frontend/
  - Vue 前端（Vue 3 + Vite + TypeScript）
- algorithm/
  - Python 算法实现（scikit-learn 等）

## 后端
- Spring Boot 3.5.x（基于 Spring Boot 3.x，JDK 17）
- Mybatis-Plus（ORM，简化开发）
- Spring Security + JWT（鉴权）
- MySQL 8.2（使用数据库`freshmart_irs`）
- Redis 6.2.14（缓存）
- Knife4j OpenAPI（Swagger UI 自动生成）
- Lombok（减少样板代码）
- Maven（构建工具）
- SLF4J + Logback（日志框架）

## 前端
- Vue 3.4.x（Composition API）
- Vite 5（构建工具）
- Element Plus 2.x（组件库）
- Vue Router 4（路由）
- Pinia（状态管理）
- Axios（HTTP客户端）
- ECharts 5（图表）

## 算法
- Python 3.12
- scikit-learn（线性回归/岭回归）
- pandas（数据处理）
- numpy（数值计算）

## 云部署（暂不考虑）
- Docker（容器化）
- Docker Compose（多容器编排）
- Nginx（反向代理）