# 企业级开发约束规则

## 代码结构与分层
1. Java：Controller-Service-Mapper三层架构，强制区分PO/DTO/VO，严禁实体类透传。
2. Vue：views与components分离，逻辑提取至composables，Pinia管理共享状态。
3. Python：数据加载-训练-预测模块化，核心逻辑封装为类。

## 命名规范
1. 类名PascalCase，变量与方法camelCase，禁止拼音命名。
2. 数据库与Python使用snake_case，Mapper方法需与XML ID完全一致。
3. 查询方法以select开头，更新方法以update开头。

## API规范
1. 遵循RESTful，资源使用复数名词。
2. 统一响应体：{code, data, message, timestamp}。
3. 必须配置Knife4j注解，确保Swagger文档自动生成。

## 数据一致性
1. 多表操作强制在Service层标注@Transactional。
2. 复杂逻辑严禁写在XML中，利用Mybatis-Plus封装CRUD。
3. 敏感字段使用@Version乐观锁或分布式锁控制并发。

## 质量门槛
1. 后端单测覆盖率不低于60%，前端请求必须具备TS类型定义。
2. 强制开启ESLint、Checkstyle、Ruff静态扫描。
3. 算法模型上线前须通过交叉验证。

## 异常与日志
1. 全局拦截BizException，严禁在代码中打印堆栈至控制台。
2. ERROR日志必带完整堆栈，INFO记录核心业务流转。
3. 全链路日志需携带Trace-Id。

## 安全与鉴权
1. 采用JWT无状态方案，请求头统一携带Bearer Token。
2. 手机号、身份证等VO字段必须脱敏。
3. 后端接口必须使用@Valid校验输入合法性。

## 提交与文档
1. Git提交遵循Angular规范，包含feat、fix、refactor等前缀。
2. 严禁直推主分支，所有变更需经过Merge Request审计。
3. README需包含uv、npm、maven的具体启动指令。