# API Style Guide（接口规范骨架）+ 接口清单（按模块分组）

> 固定前缀：`/api/v1`  
> 风格：RESTful、资源复数名词、短横线（kebab-case）  
> 鉴权：统一 `Authorization: Bearer <JWT>`，以角色控制访问（不分端前缀）

---

## 1) 统一响应体结构

### 1.1 成功响应
```json
{
  "code": 0,
  "message": "OK",
  "data": {},
  "traceId": "b1d2e3f4..."
}
```

### 1.2 失败响应
```json
{
  "code": 40001,
  "message": "参数错误：username 不能为空",
  "data": null,
  "traceId": "b1d2e3f4..."
}
```

### 1.3 约定
- `code=0` 表示业务成功；`code!=0` 表示业务失败。
- `message` 面向用户可读（可用于前端提示）。
- `traceId` 用于日志关联与排障（后端生成并在日志中可检索）。

---

## 2) 资源命名规则

### 2.1 URL 规则（冻结）
- 统一前缀：`/api/v1`
- 资源使用复数名词：`/users`、`/roles`、`/products`
- 路径风格：短横线：`/stock-in-orders`、`/inventory-batches`
- 通过层级表达从属：`/stock-in-orders/{id}/items`

### 2.2 Method 语义（冻结）
- `GET`：查询资源（列表/详情）
- `POST`：创建资源或触发动作（例如生成建议采购单）
- `PUT`：整体更新（如用于更新商品全量字段）
- `PATCH`：部分更新（如仅更新状态）
- `DELETE`：禁止物理删除；语义固定为“禁用/下架”
  - `DELETE /users/{id}`：将用户置为 `status=DISABLED`
  - `DELETE /products/{id}`：将商品置为 `status=OFF_SALE`

---

## 3) 分页规则

### 3.1 请求参数（冻结）
- `page`：从 1 开始（必填或默认 1）
- `size`：默认 10
- `sort`：可重复传参，格式：`sort=field,asc|desc`
  - 示例：`?page=1&size=10&sort=createdAt,desc&sort=id,asc`

### 3.2 分页响应 data 结构（冻结）
```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "page": 1,
    "size": 10,
    "total": 123,
    "items": []
  },
  "traceId": "..."
}
```

---

## 4) 错误码规范

### 4.1 错误码与 HTTP 状态（冻结）
- `40001`：参数错误（HTTP 400）
- `40100`：未登录/令牌无效（HTTP 401）
- `40300`：无权限（HTTP 403）
- `40400`：资源不存在（HTTP 404）
- `50000`：服务器异常（HTTP 500）

### 4.2 错误返回示例
```json
{
  "code": 40300,
  "message": "无权限：需要角色 STORE_MANAGER",
  "data": null,
  "traceId": "..."
}
```

---

## 5) 鉴权头规范与文档标注规范

### 5.1 鉴权头（冻结）
- Header：`Authorization: Bearer <JWT>`
- 未携带/无效令牌：返回 `40100`

### 5.2 接口文档标注规则（冻结）
在接口清单中每个接口必须显式写出：
- `鉴权`：`否` / `是（JWT）`
- `角色`：若需鉴权必须列出允许角色集合（如 `ADMIN/STORE_MANAGER/WAREHOUSE/CASHIER/CUSTOMER`）

---

## 6) 接口清单（按模块分组）

> 字段写到可验收：覆盖主键、状态、关键数量/金额/时间字段；不追求完整 DTO。

### 6.1 Auth

#### POST /api/v1/auth/login
- 简述：登录并获取 JWT
- 鉴权：否
- 请求字段：`username`，`password`
- 响应字段：`token`，`tokenType`（固定 `Bearer`），`expiresIn`（秒），`userId`，`username`，`roles[]`

#### POST /api/v1/auth/register
- 简述：顾客注册（默认赋予 CUSTOMER 角色）
- 鉴权：否
- 请求字段：`username`，`password`，`phone`（可选），`email`（可选）
- 约束：`phone` 若传入需为中国大陆 11 位；`email` 若传入需满足邮箱格式
- 响应字段：`id`

#### POST /api/v1/auth/logout
- 简述：登出（服务端不做 Token 黑名单；用于统一前端退出动作与审计落点）
- 鉴权：是（JWT）
- 角色：ADMIN/STORE_MANAGER/WAREHOUSE/CASHIER/CUSTOMER
- 响应字段：无（`data=null`）

#### GET /api/v1/auth/me
- 简述：获取当前登录用户信息
- 鉴权：是（JWT）
- 角色：ADMIN/STORE_MANAGER/WAREHOUSE/CASHIER/CUSTOMER
- 响应字段：`id`，`username`，`phone`，`email`，`status`，`roles[]`

#### GET /api/v1/auth/permissions
- 简述：获取当前登录用户的角色列表（仅 roles[]）
- 鉴权：是（JWT）
- 角色：ADMIN/STORE_MANAGER/WAREHOUSE/CASHIER/CUSTOMER
- 响应字段：`roles[]`

---

### 6.2 Users & Roles

#### GET /api/v1/users
- 简述：用户列表（后台）
- 鉴权：是（JWT）
- 角色：ADMIN
- 查询字段：`keyword`（用户名/手机号模糊），`status`（ENABLED/DISABLED，可选），分页参数
- 响应字段（items）：`id`，`username`，`phone`，`email`，`status`，`createdAt`

#### GET /api/v1/users/{id}
- 简述：用户详情（后台）
- 鉴权：是（JWT）
- 角色：ADMIN
- 响应字段：`id`，`username`，`phone`，`email`，`status`，`roles[]`

#### POST /api/v1/users
- 简述：创建用户（后台创建员工/顾客）
- 鉴权：是（JWT）
- 角色：ADMIN
- 请求字段：`username`，`password`，`phone`（可选），`email`（可选），`roleIds[]`（必填，覆盖式分配）
- 约束：`phone` 若传入需为中国大陆 11 位；`email` 若传入需满足邮箱格式
- 响应字段：`id`

#### PATCH /api/v1/users/{id}
- 简述：更新用户信息（复用：用户自助更新 / 管理员更新与重置密码）
- 鉴权：是（JWT）
- 角色：ADMIN/STORE_MANAGER/WAREHOUSE/CASHIER/CUSTOMER
- 约束：
  - 用户自助更新：仅允许更新自己的信息（`id` 必须等于当前登录用户）；必须提交 `currentPassword`；`username` 做唯一校验；不可自助修改 `roles/status`
  - 管理员更新：可更新用户信息；可重置密码（无需 `currentPassword`）；`username` 做唯一校验
- 请求字段（可选集合，按场景取用）：`username`，`phone`，`email`，`currentPassword`，`newPassword`，`status`
- 约束：`phone/email` 若传入需满足格式校验
- 响应字段：`id`

#### DELETE /api/v1/users/{id}
- 简述：禁用用户（语义固定：status=DISABLED）
- 鉴权：是（JWT）
- 角色：ADMIN
- 响应字段：`id`

#### GET /api/v1/roles
- 简述：角色列表
- 鉴权：是（JWT）
- 角色：ADMIN
- 响应字段（items）：`id`，`code`，`name`，`enabled`

#### PUT /api/v1/users/{id}/roles
- 简述：为用户分配角色（覆盖式）
- 鉴权：是（JWT）
- 角色：ADMIN
- 请求字段：`roleIds[]`
- 响应字段：`id`

---

### 6.3 Products

#### GET /api/v1/products
- 简述：商品列表（前台可用于浏览）
- 鉴权：否
- 查询字段：`keyword`（名称模糊），`status`（0/1，可选），分页参数
- 响应字段（items）：`id`，`sku`，`name`，`unit`，`price`，`status`

#### GET /api/v1/products/{id}
- 简述：商品详情
- 鉴权：否
- 响应字段：`id`，`sku`，`name`，`unit`，`price`，`safetyStockQty`，`status`

#### POST /api/v1/products
- 简述：创建商品
- 鉴权：是（JWT）
- 角色：ADMIN/STORE_MANAGER
- 请求字段：`sku`，`name`，`unit`，`price`，`safetyStockQty`
- 响应字段：`id`

#### PUT /api/v1/products/{id}
- 简述：更新商品（全量）
- 鉴权：是（JWT）
- 角色：ADMIN/STORE_MANAGER
- 请求字段：`name`，`unit`，`price`，`safetyStockQty`，`status`
- 响应字段：`id`

#### DELETE /api/v1/products/{id}
- 简述：下架商品（语义固定：status=OFF_SALE）
- 鉴权：是（JWT）
- 角色：ADMIN/STORE_MANAGER
- 响应字段：`id`，`status`

---

### 6.4 Inventory

#### GET /api/v1/inventory-batches
- 简述：库存批次列表（含效期）
- 鉴权：是（JWT）
- 角色：STORE_MANAGER/WAREHOUSE/CASHIER
- 查询字段：`productId`，`status`（0/1/2），`expiryFrom`，`expiryTo`，分页参数
- 响应字段（items）：`id`，`productId`，`batchNo`，`expiryDate`，`qtyInitial`，`qtyAvailable`，`status`，`receivedAt`

#### GET /api/v1/inventory-batches/{id}
- 简述：库存批次详情
- 鉴权：是（JWT）
- 角色：STORE_MANAGER/WAREHOUSE/CASHIER
- 响应字段：同上 + `productionDate`

#### GET /api/v1/inventory-ledgers
- 简述：库存流水列表（批次可追溯）
- 鉴权：是（JWT）
- 角色：STORE_MANAGER/WAREHOUSE
- 查询字段：`productId`，`batchId`，`changeType`，`sourceType`，`eventFrom`，`eventTo`，分页参数
- 响应字段（items）：`id`，`productId`，`inventoryBatchId`，`changeType`，`changeQty`，`qtyAfter`，`sourceType`，`sourceId`，`eventTime`

---

### 6.5 Stock-In

#### POST /api/v1/stock-in-orders
- 简述：创建入库单（草稿/确认）
- 鉴权：是（JWT）
- 角色：WAREHOUSE/STORE_MANAGER
- 请求字段：
  - `status`：`DRAFT` 或 `CONFIRMED`
  - `receivedAt`（确认时必填）
  - `items[]`：`productId`，`qty`，`unitCost`，`productionDate`（可空），`expiryDate`
- 响应字段：`id`，`bizNo`，`status`

#### GET /api/v1/stock-in-orders
- 简述：入库单列表
- 鉴权：是（JWT）
- 角色：WAREHOUSE/STORE_MANAGER
- 查询字段：`status`，`bizNo`，`createdBy`，`receivedFrom`，`receivedTo`，分页参数
- 响应字段（items）：`id`，`bizNo`，`createdBy`，`status`，`receivedAt`，`createdAt`

#### GET /api/v1/stock-in-orders/{id}
- 简述：入库单详情（含明细与批次）
- 鉴权：是（JWT）
- 角色：WAREHOUSE/STORE_MANAGER
- 响应字段：
  - 头：`id`，`bizNo`，`createdBy`，`status`，`receivedAt`
  - 明细：`items[]`：`id`，`productId`，`inventoryBatchId`，`qty`，`unitCost`，`expiryDate`

---

### 6.6 Sales

#### POST /api/v1/sale-orders
- 简述：创建销售单（收银出单并扣减库存）
- 鉴权：是（JWT）
- 角色：CASHIER/STORE_MANAGER
- 请求字段：
  - `saleTime`
  - `items[]`：`productId`，`inventoryBatchId`，`qty`，`unitPrice`
- 响应字段：`id`，`bizNo`，`status`（COMPLETED），`totalAmount`

#### GET /api/v1/sale-orders
- 简述：销售单列表
- 鉴权：是（JWT）
- 角色：CASHIER/STORE_MANAGER
- 查询字段：`bizNo`，`status`，`cashierId`，`saleFrom`，`saleTo`，分页参数
- 响应字段（items）：`id`，`bizNo`，`cashierId`，`status`，`totalAmount`，`saleTime`

#### GET /api/v1/sale-orders/{id}
- 简述：销售单详情（含明细）
- 鉴权：是（JWT）
- 角色：CASHIER/STORE_MANAGER
- 响应字段：
  - 头：`id`，`bizNo`，`cashierId`，`status`，`totalAmount`，`saleTime`
  - 明细：`items[]`：`id`，`productId`，`inventoryBatchId`，`qty`，`unitPrice`，`lineAmount`

---

### 6.7 Customer Orders & Payment

#### GET /api/v1/carts/me
- 简述：获取我的购物车（当前顾客）
- 鉴权：是（JWT）
- 角色：CUSTOMER
- 响应字段：`id`，`status`，`items[]`（`productId`，`qty`）

#### PUT /api/v1/carts/me/items/{productId}
- 简述：设置购物车商品数量（覆盖式）
- 鉴权：是（JWT）
- 角色：CUSTOMER
- 请求字段：`qty`
- 响应字段：`cartId`，`productId`，`qty`

#### DELETE /api/v1/carts/me/items/{productId}
- 简述：移除购物车商品
- 鉴权：是（JWT）
- 角色：CUSTOMER
- 响应字段：`cartId`，`productId`

#### POST /api/v1/customer-orders
- 简述：创建顾客订单（从购物车结算生成）
- 鉴权：是（JWT）
- 角色：CUSTOMER
- 请求字段：`source`（固定 `CART`），`items[]`（可选：服务端可直接取购物车；验收字段：`productId`，`qty`）
- 响应字段：`id`，`bizNo`，`orderStatus`（PLACED），`totalAmount`，`placedAt`

#### GET /api/v1/customer-orders
- 简述：我的订单列表
- 鉴权：是（JWT）
- 角色：CUSTOMER
- 查询字段：`status`（PLACED/PAID/CONFIRMED/CANCELLED），分页参数
- 响应字段（items）：`id`，`bizNo`，`orderStatus`，`totalAmount`，`placedAt`

#### GET /api/v1/customer-orders/{id}
- 简述：订单详情（含明细与支付信息）
- 鉴权：是（JWT）
- 角色：CUSTOMER/STORE_MANAGER
- 响应字段：
  - 订单：`id`，`bizNo`，`userId`，`orderStatus`，`totalAmount`，`placedAt`，`confirmedAt`
  - 明细：`items[]`：`productId`，`inventoryBatchId`，`qty`，`unitPrice`，`lineAmount`
  - 支付：`payment`：`id`，`payStatus`，`amount`，`paidAt`

#### POST /api/v1/customer-orders/{id}/payment
- 简述：模拟支付确认（创建/更新 payment 并驱动订单进入 PAID）
- 鉴权：是（JWT）
- 角色：CUSTOMER
- 请求字段：`amount`，`payRef`（可选）
- 响应字段：`paymentId`，`payStatus`（SUCCESS/FAILED），`orderStatus`（PAID 或保持原状态）

#### GET /api/v1/payments/{id}
- 简述：查询支付记录（模拟）
- 鉴权：是（JWT）
- 角色：CUSTOMER/STORE_MANAGER/ADMIN
- 响应字段：`id`，`customerOrderId`，`payStatus`，`amount`，`paidAt`，`payRef`

---

### 6.8 Alerts

#### GET /api/v1/alerts
- 简述：预警列表
- 鉴权：是（JWT）
- 角色：STORE_MANAGER/WAREHOUSE
- 查询字段：`alertType`（LOW_STOCK/NEAR_EXPIRY/EXPIRED），`status`（OPEN/ACKED/RESOLVED/CLOSED），`productId`，`batchId`，分页参数
- 响应字段（items）：`id`，`alertType`，`alertStatus`，`productId`，`inventoryBatchId`，`thresholdQty`，`expiryDate`，`triggeredAt`，`resolvedAt`

#### GET /api/v1/alerts/{id}
- 简述：预警详情（含处置流水）
- 鉴权：是（JWT）
- 角色：STORE_MANAGER/WAREHOUSE
- 响应字段：
  - 预警：同列表字段
  - 处置：`actions[]`：`id`，`actorUserId`，`actionType`，`actionResult`，`actionTime`

#### POST /api/v1/alerts/{id}/actions
- 简述：新增预警处置动作
- 鉴权：是（JWT）
- 角色：STORE_MANAGER/WAREHOUSE
- 请求字段：`actionType`（ACK/DISPOSE/PROMOTE/RETURN/OTHER），`actionResult`
- 响应字段：`id`，`alertId`，`actorUserId`，`actionType`，`actionTime`

---

### 6.9 Replenishment

#### POST /api/v1/replenishment-suggestions/generate
- 简述：触发预测并生成建议采购单（触发本地 Python 预测脚本）
- 鉴权：是（JWT）
- 角色：STORE_MANAGER
- 请求字段：`cycleDays`（补货周期天数），`safetyStockFactor`（可选：默认 1.0），`productIds[]`（可选：为空表示全量）
- 响应字段：`id`，`bizNo`，`approvalStatus`（PENDING），`generatedAt`

#### GET /api/v1/replenishment-suggestions
- 简述：建议采购单列表
- 鉴权：是（JWT）
- 角色：STORE_MANAGER/WAREHOUSE
- 查询字段：`status`（PENDING/APPROVED/REJECTED），`generatedFrom`，`generatedTo`，分页参数
- 响应字段（items）：`id`，`bizNo`，`approvalStatus`，`approvedBy`，`approvedAt`，`generatedAt`

#### GET /api/v1/replenishment-suggestions/{id}
- 简述：建议采购单详情（含明细）
- 鉴权：是（JWT）
- 角色：STORE_MANAGER/WAREHOUSE
- 响应字段：
  - 单头：`id`，`bizNo`，`approvalStatus`，`approvedBy`，`approvedAt`，`rejectReason`，`generatedAt`
  - 明细：`items[]`：`productId`，`predictedQty`，`cycleDays`，`safetyStockQty`，`currentStockQty`，`suggestedQty`

#### POST /api/v1/replenishment-suggestions/{id}/approve
- 简述：店长审批通过（使库管可执行采购入库）
- 鉴权：是（JWT）
- 角色：STORE_MANAGER
- 请求字段：`approvedAt`（可选：默认当前时间）
- 响应字段：`id`，`approvalStatus`（APPROVED），`approvedBy`，`approvedAt`

#### POST /api/v1/replenishment-suggestions/{id}/reject
- 简述：店长驳回
- 鉴权：是（JWT）
- 角色：STORE_MANAGER
- 请求字段：`rejectReason`
- 响应字段：`id`，`approvalStatus`（REJECTED），`approvedBy`，`approvedAt`，`rejectReason`
