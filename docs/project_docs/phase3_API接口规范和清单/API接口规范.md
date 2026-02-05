# API 接口规范

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
