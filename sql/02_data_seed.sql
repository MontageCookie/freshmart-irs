USE freshmart_irs;

SET NAMES utf8mb4;

-- =========================
-- 0) 角色
-- =========================
INSERT INTO `role` (`code`, `name`, `status`)
VALUES
  ('ADMIN', '系统管理员', 1),
  ('STORE_MANAGER', '店长', 1),
  ('WAREHOUSE', '库管', 1),
  ('CASHIER', '收银', 1),
  ('CUSTOMER', '顾客', 1)
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `status` = VALUES(`status`),
  `updated_at` = CURRENT_TIMESTAMP;

SELECT `id` INTO @role_admin FROM `role` WHERE `code`='ADMIN';
SELECT `id` INTO @role_store_manager FROM `role` WHERE `code`='STORE_MANAGER';
SELECT `id` INTO @role_warehouse FROM `role` WHERE `code`='WAREHOUSE';
SELECT `id` INTO @role_cashier FROM `role` WHERE `code`='CASHIER';
SELECT `id` INTO @role_customer FROM `role` WHERE `code`='CUSTOMER';

-- =========================
-- 1) 用户
-- =========================
INSERT INTO `user` (`username`, `password_hash`, `phone`, `email`, `status`)
VALUES
  ('admin', '$2a$12$HFLQ6bK36ICmXmz6w9cHMeR6FhSwqimABxOxirxOqVHTXjLAcGOva', '13800000001', 'admin@freshmart.test', 1),
  ('manager', '$2a$12$HFLQ6bK36ICmXmz6w9cHMeR6FhSwqimABxOxirxOqVHTXjLAcGOva', '13800000002', 'manager@freshmart.test', 1),
  ('warehouse', '$2a$12$HFLQ6bK36ICmXmz6w9cHMeR6FhSwqimABxOxirxOqVHTXjLAcGOva', '13800000003', 'warehouse@freshmart.test', 1),
  ('cashier', '$2a$12$HFLQ6bK36ICmXmz6w9cHMeR6FhSwqimABxOxirxOqVHTXjLAcGOva', '13800000004', 'cashier@freshmart.test', 1),
  ('customer1', '$2a$12$HFLQ6bK36ICmXmz6w9cHMeR6FhSwqimABxOxirxOqVHTXjLAcGOva', '13800000005', 'customer1@freshmart.test', 1)
ON DUPLICATE KEY UPDATE
  `password_hash` = VALUES(`password_hash`),
  `phone` = VALUES(`phone`),
  `email` = VALUES(`email`),
  `status` = VALUES(`status`),
  `updated_at` = CURRENT_TIMESTAMP;

SELECT `id` INTO @u_admin FROM `user` WHERE `username`='admin';
SELECT `id` INTO @u_manager FROM `user` WHERE `username`='manager';
SELECT `id` INTO @u_warehouse FROM `user` WHERE `username`='warehouse';
SELECT `id` INTO @u_cashier FROM `user` WHERE `username`='cashier';
SELECT `id` INTO @u_customer1 FROM `user` WHERE `username`='customer1';

INSERT INTO `user_role` (`user_id`, `role_id`)
VALUES
  (@u_admin, @role_admin),
  (@u_manager, @role_store_manager),
  (@u_warehouse, @role_warehouse),
  (@u_cashier, @role_cashier),
  (@u_customer1, @role_customer)
ON DUPLICATE KEY UPDATE
  `updated_at` = CURRENT_TIMESTAMP;

-- =========================
-- 2) 商品
-- =========================
INSERT INTO `product` (`sku`, `name`, `unit`, `price`, `safety_stock_qty`, `status`)
VALUES
  ('APPLE-001', '苹果', 'kg', 12.50, 20, 1),
  ('BANANA-001', '香蕉', 'kg', 8.90, 30, 1),
  ('MILK-001', '牛奶', '盒', 4.80, 50, 1)
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `unit` = VALUES(`unit`),
  `price` = VALUES(`price`),
  `safety_stock_qty` = VALUES(`safety_stock_qty`),
  `status` = VALUES(`status`),
  `updated_at` = CURRENT_TIMESTAMP;

SELECT `id` INTO @p_apple FROM `product` WHERE `sku`='APPLE-001';
SELECT `id` INTO @p_banana FROM `product` WHERE `sku`='BANANA-001';
SELECT `id` INTO @p_milk FROM `product` WHERE `sku`='MILK-001';

-- =========================
-- 3) 入库：入库单、批次、明细、库存流水（入库）
-- =========================
INSERT INTO `stock_in_order` (`biz_no`, `created_by`, `status`, `received_at`, `remark`)
VALUES
  ('SEED-SI-20260203-0001', @u_warehouse, 'CONFIRMED', '2026-02-03 10:00:00', '初始化入库（示例）')
ON DUPLICATE KEY UPDATE
  `created_by` = VALUES(`created_by`),
  `status` = VALUES(`status`),
  `received_at` = VALUES(`received_at`),
  `remark` = VALUES(`remark`),
  `updated_at` = CURRENT_TIMESTAMP;

SELECT `id` INTO @si_order_id FROM `stock_in_order` WHERE `biz_no`='SEED-SI-20260203-0001';

INSERT INTO `inventory_batch` (`product_id`, `batch_no`, `production_date`, `expiry_date`, `qty_initial`, `qty_available`, `status`, `received_at`)
VALUES
  (@p_apple, 'SEED-BATCH-APPLE-001', '2026-02-01', '2026-02-20', 50, 50, 1, '2026-02-03 10:00:00'),
  (@p_banana, 'SEED-BATCH-BANANA-001', '2026-02-01', '2026-02-15', 80, 80, 1, '2026-02-03 10:00:00'),
  (@p_milk, 'SEED-BATCH-MILK-001', NULL, '2026-03-01', 120, 120, 1, '2026-02-03 10:00:00')
ON DUPLICATE KEY UPDATE
  `production_date` = VALUES(`production_date`),
  `expiry_date` = VALUES(`expiry_date`),
  `qty_initial` = VALUES(`qty_initial`),
  `qty_available` = VALUES(`qty_available`),
  `status` = VALUES(`status`),
  `received_at` = VALUES(`received_at`),
  `updated_at` = CURRENT_TIMESTAMP;

SELECT `id` INTO @b_apple_1 FROM `inventory_batch` WHERE `product_id`=@p_apple AND `batch_no`='SEED-BATCH-APPLE-001';
SELECT `id` INTO @b_banana_1 FROM `inventory_batch` WHERE `product_id`=@p_banana AND `batch_no`='SEED-BATCH-BANANA-001';
SELECT `id` INTO @b_milk_1 FROM `inventory_batch` WHERE `product_id`=@p_milk AND `batch_no`='SEED-BATCH-MILK-001';

INSERT INTO `stock_in_item` (`stock_in_order_id`, `product_id`, `inventory_batch_id`, `qty`, `unit_cost`, `production_date`, `expiry_date`)
SELECT @si_order_id, @p_apple, @b_apple_1, 50, 8.00, '2026-02-01', '2026-02-20'
WHERE NOT EXISTS (
  SELECT 1 FROM `stock_in_item`
  WHERE `stock_in_order_id`=@si_order_id AND `product_id`=@p_apple AND `inventory_batch_id`=@b_apple_1
);

INSERT INTO `stock_in_item` (`stock_in_order_id`, `product_id`, `inventory_batch_id`, `qty`, `unit_cost`, `production_date`, `expiry_date`)
SELECT @si_order_id, @p_banana, @b_banana_1, 80, 5.50, '2026-02-01', '2026-02-15'
WHERE NOT EXISTS (
  SELECT 1 FROM `stock_in_item`
  WHERE `stock_in_order_id`=@si_order_id AND `product_id`=@p_banana AND `inventory_batch_id`=@b_banana_1
);

INSERT INTO `stock_in_item` (`stock_in_order_id`, `product_id`, `inventory_batch_id`, `qty`, `unit_cost`, `production_date`, `expiry_date`)
SELECT @si_order_id, @p_milk, @b_milk_1, 120, 3.20, NULL, '2026-03-01'
WHERE NOT EXISTS (
  SELECT 1 FROM `stock_in_item`
  WHERE `stock_in_order_id`=@si_order_id AND `product_id`=@p_milk AND `inventory_batch_id`=@b_milk_1
);

SELECT `id` INTO @sii_apple_id FROM `stock_in_item` WHERE `stock_in_order_id`=@si_order_id AND `product_id`=@p_apple AND `inventory_batch_id`=@b_apple_1;
SELECT `id` INTO @sii_banana_id FROM `stock_in_item` WHERE `stock_in_order_id`=@si_order_id AND `product_id`=@p_banana AND `inventory_batch_id`=@b_banana_1;
SELECT `id` INTO @sii_milk_id FROM `stock_in_item` WHERE `stock_in_order_id`=@si_order_id AND `product_id`=@p_milk AND `inventory_batch_id`=@b_milk_1;

INSERT INTO `inventory_ledger` (`product_id`, `inventory_batch_id`, `change_type`, `change_qty`, `qty_after`, `source_type`, `source_id`, `event_time`)
SELECT
  @p_apple, @b_apple_1, 'STOCK_IN', 50,
  (SELECT `qty_available` FROM `inventory_batch` WHERE `id`=@b_apple_1),
  'STOCK_IN_ITEM', @sii_apple_id, '2026-02-03 10:05:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `inventory_ledger` WHERE `source_type`='STOCK_IN_ITEM' AND `source_id`=@sii_apple_id
);

INSERT INTO `inventory_ledger` (`product_id`, `inventory_batch_id`, `change_type`, `change_qty`, `qty_after`, `source_type`, `source_id`, `event_time`)
SELECT
  @p_banana, @b_banana_1, 'STOCK_IN', 80,
  (SELECT `qty_available` FROM `inventory_batch` WHERE `id`=@b_banana_1),
  'STOCK_IN_ITEM', @sii_banana_id, '2026-02-03 10:05:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `inventory_ledger` WHERE `source_type`='STOCK_IN_ITEM' AND `source_id`=@sii_banana_id
);

INSERT INTO `inventory_ledger` (`product_id`, `inventory_batch_id`, `change_type`, `change_qty`, `qty_after`, `source_type`, `source_id`, `event_time`)
SELECT
  @p_milk, @b_milk_1, 'STOCK_IN', 120,
  (SELECT `qty_available` FROM `inventory_batch` WHERE `id`=@b_milk_1),
  'STOCK_IN_ITEM', @sii_milk_id, '2026-02-03 10:05:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `inventory_ledger` WHERE `source_type`='STOCK_IN_ITEM' AND `source_id`=@sii_milk_id
);

-- =========================
-- 4) 收银销售：销售单、明细、库存流水（扣减示例）
-- =========================
INSERT INTO `sale_order` (`biz_no`, `cashier_id`, `status`, `total_amount`, `sale_time`)
VALUES
  ('SEED-SO-20260203-0001', @u_cashier, 'COMPLETED', 25.00, '2026-02-03 11:00:00')
ON DUPLICATE KEY UPDATE
  `cashier_id` = VALUES(`cashier_id`),
  `status` = VALUES(`status`),
  `total_amount` = VALUES(`total_amount`),
  `sale_time` = VALUES(`sale_time`),
  `updated_at` = CURRENT_TIMESTAMP;

SELECT `id` INTO @sale_order_id FROM `sale_order` WHERE `biz_no`='SEED-SO-20260203-0001';

INSERT INTO `sale_item` (`sale_order_id`, `product_id`, `inventory_batch_id`, `qty`, `unit_price`, `line_amount`)
SELECT @sale_order_id, @p_apple, @b_apple_1, 2, 12.50, 25.00
WHERE NOT EXISTS (
  SELECT 1 FROM `sale_item` WHERE `sale_order_id`=@sale_order_id AND `product_id`=@p_apple AND `inventory_batch_id`=@b_apple_1
);

SELECT `id` INTO @sale_item_apple_id FROM `sale_item` WHERE `sale_order_id`=@sale_order_id AND `product_id`=@p_apple AND `inventory_batch_id`=@b_apple_1;

INSERT INTO `inventory_ledger` (`product_id`, `inventory_batch_id`, `change_type`, `change_qty`, `qty_after`, `source_type`, `source_id`, `event_time`)
SELECT
  @p_apple, @b_apple_1, 'SALE_POS', -2,
  (SELECT `qty_available` FROM `inventory_batch` WHERE `id`=@b_apple_1),
  'SALE_ITEM', @sale_item_apple_id, '2026-02-03 11:00:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `inventory_ledger` WHERE `source_type`='SALE_ITEM' AND `source_id`=@sale_item_apple_id
);

-- =========================
-- 5) 顾客购买：购物车、订单、订单明细、模拟支付
-- =========================
INSERT INTO `cart` (`user_id`, `status`)
VALUES
  (@u_customer1, 'ACTIVE')
ON DUPLICATE KEY UPDATE
  `status` = VALUES(`status`),
  `updated_at` = CURRENT_TIMESTAMP;

SELECT `id` INTO @cart_id FROM `cart` WHERE `user_id`=@u_customer1;

INSERT INTO `cart_item` (`cart_id`, `product_id`, `qty`)
VALUES
  (@cart_id, @p_banana, 3),
  (@cart_id, @p_milk, 2)
ON DUPLICATE KEY UPDATE
  `qty` = VALUES(`qty`),
  `updated_at` = CURRENT_TIMESTAMP;

INSERT INTO `customer_order` (`biz_no`, `user_id`, `order_status`, `total_amount`, `placed_at`, `confirmed_at`, `cancelled_at`)
VALUES
  ('SEED-CO-20260203-0001', @u_customer1, 'CONFIRMED', 35.10, '2026-02-03 12:00:00', '2026-02-03 12:05:00', NULL)
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `order_status` = VALUES(`order_status`),
  `total_amount` = VALUES(`total_amount`),
  `placed_at` = VALUES(`placed_at`),
  `confirmed_at` = VALUES(`confirmed_at`),
  `cancelled_at` = VALUES(`cancelled_at`),
  `updated_at` = CURRENT_TIMESTAMP;

SELECT `id` INTO @co_id FROM `customer_order` WHERE `biz_no`='SEED-CO-20260203-0001';

INSERT INTO `customer_order_item` (`customer_order_id`, `product_id`, `inventory_batch_id`, `qty`, `unit_price`, `line_amount`)
SELECT @co_id, @p_banana, @b_banana_1, 3, 8.90, 26.70
WHERE NOT EXISTS (
  SELECT 1 FROM `customer_order_item` WHERE `customer_order_id`=@co_id AND `product_id`=@p_banana AND `inventory_batch_id`=@b_banana_1
);

INSERT INTO `customer_order_item` (`customer_order_id`, `product_id`, `inventory_batch_id`, `qty`, `unit_price`, `line_amount`)
SELECT @co_id, @p_milk, @b_milk_1, 2, 4.20, 8.40
WHERE NOT EXISTS (
  SELECT 1 FROM `customer_order_item` WHERE `customer_order_id`=@co_id AND `product_id`=@p_milk AND `inventory_batch_id`=@b_milk_1
);

SELECT `id` INTO @coi_banana_id FROM `customer_order_item` WHERE `customer_order_id`=@co_id AND `product_id`=@p_banana AND `inventory_batch_id`=@b_banana_1;
SELECT `id` INTO @coi_milk_id FROM `customer_order_item` WHERE `customer_order_id`=@co_id AND `product_id`=@p_milk AND `inventory_batch_id`=@b_milk_1;

INSERT INTO `payment` (`customer_order_id`, `pay_status`, `amount`, `paid_at`, `pay_ref`)
VALUES
  (@co_id, 'SUCCESS', 35.10, '2026-02-03 12:02:00', 'SEED-PAY-20260203-0001')
ON DUPLICATE KEY UPDATE
  `pay_status` = VALUES(`pay_status`),
  `amount` = VALUES(`amount`),
  `paid_at` = VALUES(`paid_at`),
  `pay_ref` = VALUES(`pay_ref`),
  `updated_at` = CURRENT_TIMESTAMP;

INSERT INTO `inventory_ledger` (`product_id`, `inventory_batch_id`, `change_type`, `change_qty`, `qty_after`, `source_type`, `source_id`, `event_time`)
SELECT
  @p_banana, @b_banana_1, 'SALE_ONLINE', -3,
  (SELECT `qty_available` FROM `inventory_batch` WHERE `id`=@b_banana_1),
  'CUSTOMER_ORDER_ITEM', @coi_banana_id, '2026-02-03 12:05:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `inventory_ledger` WHERE `source_type`='CUSTOMER_ORDER_ITEM' AND `source_id`=@coi_banana_id
);

INSERT INTO `inventory_ledger` (`product_id`, `inventory_batch_id`, `change_type`, `change_qty`, `qty_after`, `source_type`, `source_id`, `event_time`)
SELECT
  @p_milk, @b_milk_1, 'SALE_ONLINE', -2,
  (SELECT `qty_available` FROM `inventory_batch` WHERE `id`=@b_milk_1),
  'CUSTOMER_ORDER_ITEM', @coi_milk_id, '2026-02-03 12:05:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `inventory_ledger` WHERE `source_type`='CUSTOMER_ORDER_ITEM' AND `source_id`=@coi_milk_id
);

-- =========================
-- 6) 预警：低库存预警 + 处置流水
-- =========================
INSERT INTO `alert` (`alert_type`, `alert_status`, `product_id`, `inventory_batch_id`, `threshold_qty`, `expiry_date`, `triggered_at`, `resolved_at`)
SELECT
  'LOW_STOCK', 'OPEN', @p_apple, NULL, 20, NULL, '2026-02-03 13:00:00', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM `alert`
  WHERE `alert_type`='LOW_STOCK'
    AND `product_id`=@p_apple
    AND IFNULL(`inventory_batch_id`, 0)=0
    AND `triggered_at`='2026-02-03 13:00:00'
);

SELECT `id` INTO @alert_low_stock_id FROM `alert`
WHERE `alert_type`='LOW_STOCK' AND `product_id`=@p_apple AND IFNULL(`inventory_batch_id`, 0)=0 AND `triggered_at`='2026-02-03 13:00:00'
ORDER BY `id` DESC
LIMIT 1;

INSERT INTO `alert_action` (`alert_id`, `actor_user_id`, `action_type`, `action_result`, `action_time`)
SELECT
  @alert_low_stock_id, @u_manager, 'ACK', '店长已确认预警，待补货审批', '2026-02-03 13:05:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `alert_action`
  WHERE `alert_id`=@alert_low_stock_id
    AND `actor_user_id`=@u_manager
    AND `action_type`='ACK'
);

-- =========================
-- 7) 预测与补货：建议采购单（含审批）+ 明细
-- =========================
INSERT INTO `replenishment_suggestion` (`biz_no`, `approval_status`, `approved_by`, `approved_at`, `reject_reason`, `generated_at`)
VALUES
  ('SEED-RS-20260203-0001', 'APPROVED', @u_manager, '2026-02-03 14:00:00', NULL, '2026-02-03 13:50:00')
ON DUPLICATE KEY UPDATE
  `approval_status` = VALUES(`approval_status`),
  `approved_by` = VALUES(`approved_by`),
  `approved_at` = VALUES(`approved_at`),
  `reject_reason` = VALUES(`reject_reason`),
  `generated_at` = VALUES(`generated_at`),
  `updated_at` = CURRENT_TIMESTAMP;

SELECT `id` INTO @rs_id FROM `replenishment_suggestion` WHERE `biz_no`='SEED-RS-20260203-0001';

INSERT INTO `replenishment_item` (
  `replenishment_suggestion_id`, `product_id`, `predicted_qty`, `cycle_days`, `safety_stock_qty`, `current_stock_qty`, `suggested_qty`
)
SELECT
  @rs_id, @p_apple, 30, 7, 20, 10, 220
WHERE NOT EXISTS (
  SELECT 1 FROM `replenishment_item` WHERE `replenishment_suggestion_id`=@rs_id AND `product_id`=@p_apple
);

INSERT INTO `replenishment_item` (
  `replenishment_suggestion_id`, `product_id`, `predicted_qty`, `cycle_days`, `safety_stock_qty`, `current_stock_qty`, `suggested_qty`
)
SELECT
  @rs_id, @p_banana, 40, 7, 30, 15, 295
WHERE NOT EXISTS (
  SELECT 1 FROM `replenishment_item` WHERE `replenishment_suggestion_id`=@rs_id AND `product_id`=@p_banana
);
