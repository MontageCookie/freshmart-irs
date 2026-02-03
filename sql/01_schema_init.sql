CREATE DATABASE IF NOT EXISTS freshmart_irs
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE freshmart_irs;

SET NAMES utf8mb4;

-- 1) 账号与角色
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `username` VARCHAR(64) NOT NULL COMMENT '登录名（唯一）',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
  `phone` VARCHAR(32) NULL COMMENT '手机号',
  `email` VARCHAR(128) NULL COMMENT '邮箱',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '用户状态：1=ACTIVE,0=DISABLED',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  KEY `idx_user_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统一用户表（员工+顾客）';

CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `code` VARCHAR(64) NOT NULL COMMENT '角色编码（唯一，如 ADMIN/STORE_MANAGER/WAREHOUSE/CASHIER/CUSTOMER）',
  `name` VARCHAR(64) NOT NULL COMMENT '角色名称',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '角色状态：1=ACTIVE,0=DISABLED',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`code`),
  KEY `idx_role_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表（含 CUSTOMER）';

CREATE TABLE IF NOT EXISTS `user_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> user.id',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> role.id',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role_user_role` (`user_id`, `role_id`),
  KEY `idx_user_role_user_id` (`user_id`),
  KEY `idx_user_role_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-角色绑定（权限/菜单层面）';

-- 2) 商品与库存
CREATE TABLE IF NOT EXISTS `product` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `sku` VARCHAR(64) NOT NULL COMMENT 'SKU（唯一）',
  `name` VARCHAR(128) NOT NULL COMMENT '商品名称',
  `unit` VARCHAR(32) NOT NULL COMMENT '计量单位（如 kg/盒/袋）',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价（销售价）',
  `safety_stock_qty` INT NOT NULL DEFAULT 0 COMMENT '安全库存阈值（触发LOW_STOCK）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '商品状态：1=ON_SALE,0=OFF_SALE',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_sku` (`sku`),
  KEY `idx_product_status` (`status`),
  KEY `idx_product_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';

CREATE TABLE IF NOT EXISTS `inventory_batch` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> product.id',
  `batch_no` VARCHAR(64) NOT NULL COMMENT '入库批次号（业务号，可用于追溯）',
  `production_date` DATE NULL COMMENT '生产日期（可空）',
  `expiry_date` DATE NOT NULL COMMENT '效期日期',
  `qty_initial` INT NOT NULL COMMENT '批次初始入库数量',
  `qty_available` INT NOT NULL COMMENT '批次当前可用数量',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '批次状态：1=AVAILABLE,0=BLOCKED,2=EXPIRED',
  `received_at` DATETIME NOT NULL COMMENT '入库时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inventory_batch_product_batchno` (`product_id`, `batch_no`),
  KEY `idx_inventory_batch_product_id` (`product_id`),
  KEY `idx_inventory_batch_expiry_date` (`expiry_date`),
  KEY `idx_inventory_batch_status` (`status`),
  CONSTRAINT `fk_inventory_batch_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存批次（含效期）';

CREATE TABLE IF NOT EXISTS `inventory_ledger` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> product.id',
  `inventory_batch_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> inventory_batch.id（批次追溯必填）',
  `change_type` VARCHAR(32) NOT NULL COMMENT '流水类型：STOCK_IN/SALE_POS/SALE_ONLINE/ADJUST',
  `change_qty` INT NOT NULL COMMENT '变更数量（入库为正，扣减为负）',
  `qty_after` INT NOT NULL COMMENT '变更后批次可用量快照',
  `source_type` VARCHAR(32) NOT NULL COMMENT '来源单据类型：STOCK_IN_ITEM/SALE_ITEM/CUSTOMER_ORDER_ITEM/OTHER',
  `source_id` BIGINT UNSIGNED NOT NULL COMMENT '来源单据ID（与source_type配合追溯）',
  `event_time` DATETIME NOT NULL COMMENT '业务发生时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_inventory_ledger_product_id` (`product_id`),
  KEY `idx_inventory_ledger_batch_id` (`inventory_batch_id`),
  KEY `idx_inventory_ledger_change_type` (`change_type`),
  KEY `idx_inventory_ledger_event_time` (`event_time`),
  KEY `idx_inventory_ledger_source` (`source_type`, `source_id`),
  CONSTRAINT `fk_inventory_ledger_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_inventory_ledger_batch` FOREIGN KEY (`inventory_batch_id`) REFERENCES `inventory_batch` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存流水（批次可追溯）';

-- 3) 入库链路
CREATE TABLE IF NOT EXISTS `stock_in_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `biz_no` VARCHAR(64) NOT NULL COMMENT '入库单号（业务号）',
  `created_by` BIGINT UNSIGNED NOT NULL COMMENT '创建人（库管/店长）FK -> user.id',
  `status` VARCHAR(16) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/CONFIRMED/CANCELLED',
  `received_at` DATETIME NULL COMMENT '到货/入库确认时间',
  `remark` VARCHAR(255) NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stock_in_order_biz_no` (`biz_no`),
  KEY `idx_stock_in_order_created_by` (`created_by`),
  KEY `idx_stock_in_order_status` (`status`),
  KEY `idx_stock_in_order_received_at` (`received_at`),
  CONSTRAINT `fk_stock_in_order_created_by` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='入库单';

CREATE TABLE IF NOT EXISTS `stock_in_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `stock_in_order_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> stock_in_order.id',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> product.id',
  `inventory_batch_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> inventory_batch.id（对应入库生成的批次）',
  `qty` INT NOT NULL COMMENT '入库数量',
  `unit_cost` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '入库成本单价（可选业务视图）',
  `production_date` DATE NULL COMMENT '生产日期（可空）',
  `expiry_date` DATE NOT NULL COMMENT '效期日期',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_stock_in_item_order_id` (`stock_in_order_id`),
  KEY `idx_stock_in_item_product_id` (`product_id`),
  KEY `idx_stock_in_item_batch_id` (`inventory_batch_id`),
  CONSTRAINT `fk_stock_in_item_order` FOREIGN KEY (`stock_in_order_id`) REFERENCES `stock_in_order` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_stock_in_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_stock_in_item_batch` FOREIGN KEY (`inventory_batch_id`) REFERENCES `inventory_batch` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='入库明细';

-- 4) 销售与收银链路
CREATE TABLE IF NOT EXISTS `sale_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `biz_no` VARCHAR(64) NOT NULL COMMENT '销售单号（业务号）',
  `cashier_id` BIGINT UNSIGNED NOT NULL COMMENT '收银员 FK -> user.id',
  `status` VARCHAR(16) NOT NULL DEFAULT 'COMPLETED' COMMENT 'COMPLETED/CANCELLED',
  `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '销售总金额',
  `sale_time` DATETIME NOT NULL COMMENT '销售发生时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sale_order_biz_no` (`biz_no`),
  KEY `idx_sale_order_cashier_id` (`cashier_id`),
  KEY `idx_sale_order_status` (`status`),
  KEY `idx_sale_order_sale_time` (`sale_time`),
  CONSTRAINT `fk_sale_order_cashier` FOREIGN KEY (`cashier_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='销售单（收银）';

CREATE TABLE IF NOT EXISTS `sale_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `sale_order_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> sale_order.id',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> product.id',
  `inventory_batch_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> inventory_batch.id（扣减批次）',
  `qty` INT NOT NULL COMMENT '销售数量',
  `unit_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '成交单价',
  `line_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '行金额=qty*unit_price',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sale_item_order_id` (`sale_order_id`),
  KEY `idx_sale_item_product_id` (`product_id`),
  KEY `idx_sale_item_batch_id` (`inventory_batch_id`),
  CONSTRAINT `fk_sale_item_order` FOREIGN KEY (`sale_order_id`) REFERENCES `sale_order` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_sale_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_sale_item_batch` FOREIGN KEY (`inventory_batch_id`) REFERENCES `inventory_batch` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='销售明细（收银）';

-- 5) 顾客购买链路（含模拟支付）
CREATE TABLE IF NOT EXISTS `cart` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '顾客 FK -> user.id',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/CHECKED_OUT',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_user_id` (`user_id`),
  KEY `idx_cart_status` (`status`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车（每用户一车）';

CREATE TABLE IF NOT EXISTS `cart_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cart_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> cart.id',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> product.id',
  `qty` INT NOT NULL COMMENT '数量',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_item_cart_product` (`cart_id`, `product_id`),
  KEY `idx_cart_item_cart_id` (`cart_id`),
  KEY `idx_cart_item_product_id` (`product_id`),
  CONSTRAINT `fk_cart_item_cart` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_cart_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车明细';

CREATE TABLE IF NOT EXISTS `customer_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `biz_no` VARCHAR(64) NOT NULL COMMENT '订单号（业务号）',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '顾客 FK -> user.id',
  `order_status` VARCHAR(16) NOT NULL DEFAULT 'PLACED' COMMENT 'PLACED/PAID/CONFIRMED/CANCELLED',
  `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
  `placed_at` DATETIME NOT NULL COMMENT '下单时间',
  `confirmed_at` DATETIME NULL COMMENT '确认完成时间（扣减库存后）',
  `cancelled_at` DATETIME NULL COMMENT '取消时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_order_biz_no` (`biz_no`),
  KEY `idx_customer_order_user_id` (`user_id`),
  KEY `idx_customer_order_status` (`order_status`),
  KEY `idx_customer_order_placed_at` (`placed_at`),
  CONSTRAINT `fk_customer_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='顾客订单';

CREATE TABLE IF NOT EXISTS `customer_order_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `customer_order_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> customer_order.id',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> product.id',
  `inventory_batch_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> inventory_batch.id（扣减批次）',
  `qty` INT NOT NULL COMMENT '数量',
  `unit_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '成交单价',
  `line_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '行金额=qty*unit_price',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_order_item_order_id` (`customer_order_id`),
  KEY `idx_customer_order_item_product_id` (`product_id`),
  KEY `idx_customer_order_item_batch_id` (`inventory_batch_id`),
  CONSTRAINT `fk_customer_order_item_order` FOREIGN KEY (`customer_order_id`) REFERENCES `customer_order` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_customer_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_customer_order_item_batch` FOREIGN KEY (`inventory_batch_id`) REFERENCES `inventory_batch` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='顾客订单明细（批次可追溯）';

CREATE TABLE IF NOT EXISTS `payment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `customer_order_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> customer_order.id',
  `pay_status` VARCHAR(16) NOT NULL DEFAULT 'INIT' COMMENT 'INIT/SUCCESS/FAILED',
  `amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '支付金额',
  `paid_at` DATETIME NULL COMMENT '支付完成时间（模拟）',
  `pay_ref` VARCHAR(64) NULL COMMENT '模拟支付参考号（可选）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_order_id` (`customer_order_id`),
  KEY `idx_payment_status` (`pay_status`),
  KEY `idx_payment_paid_at` (`paid_at`),
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`customer_order_id`) REFERENCES `customer_order` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='模拟支付记录（驱动订单状态）';

-- 6) 预警
CREATE TABLE IF NOT EXISTS `alert` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `alert_type` VARCHAR(16) NOT NULL COMMENT 'LOW_STOCK/NEAR_EXPIRY/EXPIRED',
  `alert_status` VARCHAR(16) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN/ACKED/RESOLVED/CLOSED',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> product.id',
  `inventory_batch_id` BIGINT UNSIGNED NULL COMMENT 'FK -> inventory_batch.id（临期/过期预警必填）',
  `threshold_qty` INT NULL COMMENT '安全库存阈值（LOW_STOCK时可填）',
  `expiry_date` DATE NULL COMMENT '批次效期（临期/过期预警时可填）',
  `triggered_at` DATETIME NOT NULL COMMENT '触发时间',
  `resolved_at` DATETIME NULL COMMENT '解决时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_alert_type` (`alert_type`),
  KEY `idx_alert_status` (`alert_status`),
  KEY `idx_alert_product_id` (`product_id`),
  KEY `idx_alert_batch_id` (`inventory_batch_id`),
  KEY `idx_alert_triggered_at` (`triggered_at`),
  CONSTRAINT `fk_alert_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_alert_batch` FOREIGN KEY (`inventory_batch_id`) REFERENCES `inventory_batch` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预警记录（安全库存/临期/过期）';

CREATE TABLE IF NOT EXISTS `alert_action` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `alert_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> alert.id',
  `actor_user_id` BIGINT UNSIGNED NOT NULL COMMENT '处置人 FK -> user.id',
  `action_type` VARCHAR(16) NOT NULL COMMENT 'ACK/DISPOSE/PROMOTE/RETURN/OTHER',
  `action_result` VARCHAR(255) NULL COMMENT '处置结果摘要',
  `action_time` DATETIME NOT NULL COMMENT '处置时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_alert_action_alert_id` (`alert_id`),
  KEY `idx_alert_action_actor_user_id` (`actor_user_id`),
  KEY `idx_alert_action_time` (`action_time`),
  CONSTRAINT `fk_alert_action_alert` FOREIGN KEY (`alert_id`) REFERENCES `alert` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_alert_action_actor` FOREIGN KEY (`actor_user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预警处置流水';

-- 7) 预测与补货（建议采购）
CREATE TABLE IF NOT EXISTS `replenishment_suggestion` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `biz_no` VARCHAR(64) NOT NULL COMMENT '建议采购单号（业务号）',
  `approval_status` VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
  `approved_by` BIGINT UNSIGNED NULL COMMENT '店长 user_id（审批人）',
  `approved_at` DATETIME NULL COMMENT '审批时间',
  `reject_reason` VARCHAR(255) NULL COMMENT '驳回原因（可空）',
  `generated_at` DATETIME NOT NULL COMMENT '生成时间（系统生成建议时刻）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_replenishment_suggestion_biz_no` (`biz_no`),
  KEY `idx_replenishment_suggestion_status` (`approval_status`),
  KEY `idx_replenishment_suggestion_generated_at` (`generated_at`),
  KEY `idx_replenishment_suggestion_approved_by` (`approved_by`),
  CONSTRAINT `fk_replenishment_suggestion_approved_by` FOREIGN KEY (`approved_by`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='建议采购单（审批信息单表字段冻结）';

CREATE TABLE IF NOT EXISTS `replenishment_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `replenishment_suggestion_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> replenishment_suggestion.id',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT 'FK -> product.id',
  `predicted_qty` INT NOT NULL COMMENT '预测量（周期内）',
  `cycle_days` INT NOT NULL COMMENT '补货周期天数（T_cycle）',
  `safety_stock_qty` INT NOT NULL COMMENT '安全库存（S_safety）',
  `current_stock_qty` INT NOT NULL COMMENT '当前库存（I_current，聚合视图）',
  `suggested_qty` INT NOT NULL COMMENT '建议采购量（Q_suggest）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_replenishment_item_suggestion_id` (`replenishment_suggestion_id`),
  KEY `idx_replenishment_item_product_id` (`product_id`),
  CONSTRAINT `fk_replenishment_item_suggestion` FOREIGN KEY (`replenishment_suggestion_id`) REFERENCES `replenishment_suggestion` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_replenishment_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='建议采购明细';